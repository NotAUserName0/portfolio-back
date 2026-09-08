pipeline {
    agent any

    environment {
        DB_PASSWORD    = credentials('backend-db-password')
        ADMIN_PASSWORD = credentials('backend-admin-password')
        JWT_SECRET     = credentials('backend-jwt-secret')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Downloading source code...'
                checkout scm
            }
        }

        stage('Build & Deploy') {
            steps {
                echo 'Building and starting container with Docker Compose...'
                // Dockerfile runs ./mvnw clean package internally
                sh 'docker compose up -d --build --remove-orphans'
            }
        }

        stage('Cleanup') {
            steps {
                echo 'Cleaning up orphaned intermediate images...'
                sh 'docker image prune -f'
            }
        }
    }

    post {
        success {
            echo 'Backend deployment completed successfully!'
        }
        failure {
            echo 'Error in the backend pipeline. Check the logs.'
        }
    }
}