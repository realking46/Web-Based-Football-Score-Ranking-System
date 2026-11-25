// Jenkins pipeline: runs the Selenium (Java + TestNG) UI suite on every build.
// Needs a Jenkins agent with JDK 21, Maven and Google Chrome installed,
// plus the JUnit plugin (for test reports) and the HTML Publisher plugin (optional).
pipeline {
    agent any

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    triggers {
        pollSCM('H/15 * * * *')   // or use a GitHub webhook instead
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run the suite in')
        booleanParam(name: 'RUN_KNOWN_BUGS', defaultValue: false, description: 'Also run tests that reproduce open bugs')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('UI tests') {
            steps {
                dir('selenium-tests') {
                    sh "mvn -B clean test -Dbrowser=${params.BROWSER} -Dheadless=true"
                }
            }
        }

        stage('Known bugs') {
            when { expression { params.RUN_KNOWN_BUGS } }
            steps {
                dir('selenium-tests') {
                    // Expected to fail until the bugs are fixed, so don't fail the build.
                    catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                        sh "mvn -B test -DsuiteXmlFile=testng-known-bugs.xml -Dbrowser=${params.BROWSER}"
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'selenium-tests/target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'selenium-tests/target/surefire-reports/**'
        }
    }
}
