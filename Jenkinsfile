node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'latest', description: '', name: 'APP_VERSION'), string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')]), pipelineTriggers([pollSCM('H/5 * * * *')])])
  try {
   stage('Preparation') {
          cleanWs()
		  git branch: 'master', url: 'git@github.com:ca-cwds/jobs.git'
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti
   }
   stage('Build'){
		def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'jar'
   }
   stage('CoverageCheck_and_Test') {
       sh ('docker-compose up -d')
	   buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--debug -DDB_CMS_JDBC_URL=jdbc:db2://127.0.0.1:51000/DB0TDEV -DDB_CMS_PASSWORD=db2inst1 -DDB_CMS_SCHEMA=CWSCMS -DDB_CMS_USER=db2inst1 -DDB_NS_JDBC_URL=jdbc:postgresql://127.0.0.1:5432/postgres_data -DDB_NS_PASSWORD=postgres_data -DDB_NS_USER=postgres_data  -DDB_NS_SCHEMA=cwsns', tasks: 'test jacocoTestReport'
	   result = buildInfo.result
   }
   stage('SonarQube analysis'){
		withSonarQubeEnv('Core-SonarQube') {
			buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'sonarqube'
        }
    }
    stage ('Push to artifactory'){
        rtGradle.deployer repo:'libs-snapshot', server: serverArti
        rtGradle.deployer.deployArtifacts = true
        buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'artifactoryPublish'
        rtGradle.deployer.deployArtifacts = false
	}
	stage('Clean WorkSpace') {
		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dropDockerImage'
		archiveArtifacts artifacts: '**/jobs-*.jar,readme.txt', fingerprint: true
		sh ('docker-compose down -v')
	}
 } catch (e)   {
       emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
       subject: "Jobs failed with ${e.message}", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
       slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Falled: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
	   sh ('docker-compose down -v')
	   }
}

