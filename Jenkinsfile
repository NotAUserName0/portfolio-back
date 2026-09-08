pipeline {
    agent any

    environment {
        // Expose environment variables to the Docker container
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

        stage('Verify & Test') {
            steps {
                echo 'Verifying build and running unit tests with Java 21...'
                sh 'docker run --rm -v $(pwd):/app -w /app eclipse-temurin:21-jdk-alpine sh -c "chmod +x ./mvnw && ./mvnw clean test --batch-mode"'
            }
        }

        stage('Build & Deploy') {
            steps {
                echo 'Building Docker image and starting portfolio-api...'
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