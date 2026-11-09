pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    tools {
        jdk 'jdk17'   // configurar en Manage Jenkins > Tools
    }

    environment {
        IMAGE_API      = "gadbacorp/api-minimarket"
        IMAGE_REPORTES = "gadbacorp/quarkus-reportes"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('API: build + tests') {
            steps { sh 'chmod +x mvnw && ./mvnw -B -ntp clean verify' }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Reportes (Quarkus): build + tests') {
            steps { dir('quarkus-reportes') { sh 'chmod +x gradlew && ./gradlew --no-daemon build' } }
            post {
                always { junit 'quarkus-reportes/build/test-results/test/*.xml' }
            }
        }

        stage('Package') {
            when { branch 'main' }
            steps {
                sh './mvnw -B -ntp -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker images') {
            when { branch 'main' }
            steps {
                sh "docker build -t ${IMAGE_API}:${env.BUILD_NUMBER} -t ${IMAGE_API}:latest ."
                sh "docker build -t ${IMAGE_REPORTES}:${env.BUILD_NUMBER} -t ${IMAGE_REPORTES}:latest quarkus-reportes"
            }
        }
    }

    post {
        success { echo 'Pipeline OK' }
        failure { echo 'Pipeline FALLO: revisar los reportes de tests' }
    }
}
