pipeline {
    agent any

    environment {
        CI = 'true'

        DISCORD_TOKEN = credentials('WEBHOOK_TOKEN')
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

    post {
        success {
            script {
                discordSend webhookURL: "https://discordapp.com/api/webhooks/456986000447766534/${DISCORD_TOKEN}",
                            title: 'A New Development Build is Available!',
                            link: env.BUILD_URL,
                            description: "${env.BUILD_TAG}-${env.GIT_COMMIT}\n\nPlease report any issues to https://github.com/NicholasFeldman/BetterRecords/issues",
                            footer: 'Thank you for testing!'
            }
        }
    }
}
