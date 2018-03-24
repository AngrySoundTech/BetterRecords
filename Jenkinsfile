pipeline {
    agent any

    environment {
        CI = 'true'
    }

    stages {
        stage('Setup') {
            steps {
                sh './gradlew clean setupCiWorkspace'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Deploy') {
            steps {
                archiveArtifacts 'build/libs/*.jar'
            }
        }
    }
}
