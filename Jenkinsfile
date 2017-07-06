node ('dora-slave'){
   def artifactVersion="3.3-SNAPSHOT"
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'latest', description: '', name: 'APP_VERSION'), string(defaultValue: 'master', description: '', name: 'branch'), string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')]), pipelineTriggers([pollSCM('H/5 * * * *')])])
  try {
   stage('Preparation') {
          cleanWs()
		  git branch: '$branch', url: 'git@github.com:ca-cwds/jobs.git'
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti
		  rtGradle.deployer.mavenCompatible = true
		  rtGradle.deployer.deployMavenDescriptors = true
		  rtGradle.useWrapper = true
   }
   stage('Build'){
		def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'jar shadowJar'
   }
   stage('Tests and Coverage') {
       sh ('docker-compose pull')
       sh ('docker-compose up -d')
       sleep (60)
	   buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'test jacocoTestReport'
   }
   stage('SonarQube analysis'){
		withSonarQubeEnv('Core-SonarQube') {
			buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
        }
    }
    stage ('Push to artifactory'){
        rtGradle.deployer repo:'libs-snapshot', server: serverArti
        rtGradle.deployer.deployArtifacts = true
        buildInfo = rtGradle.run buildFile: 'build.gradle', switches: "--info", tasks: 'artifactoryPublish'
        rtGradle.deployer.deployArtifacts = false
	}
	stage('Clean WorkSpace') {
		archiveArtifacts artifacts: '**/jobs-*.jar,readme.txt,DocumentIndexerJob-*.jar', fingerprint: true
		sh ('docker-compose down -v')
		publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/test', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])

	}cd
 } catch (e)   {
       emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
       subject: "Jobs failed with ${e.message}", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
       slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Falled: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
	   sh ('docker-compose down -v')
	   publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/test', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])

	}
}

