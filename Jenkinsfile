node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'latest', description: '', name: 'APP_VERSION'), string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')]), pipelineTriggers([pollSCM('H/5 * * * *')])])
  try {
   stage('Preparation') {
		  git branch: 'master', url: 'git@github.com:ca-cwds/jobs.git'
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti
		  rtGradle.setUseWrapper(true)
   }
   stage('Build'){
		def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'jar'
   }
   stage('CoverageCheck_and_Test') {
     catchError {
	   buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport'
	   echo "${buildInfo}"
	 } 
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
		cleanWs()
	}
 } catch (e)   {
       emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
       subject: "Jobs failed with ${e.message}", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
       slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Falled: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
       cleanWs()
	   }
}

