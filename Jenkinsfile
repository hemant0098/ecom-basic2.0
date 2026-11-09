pipeline {
    agent any

    environment {
        DOCKER_USERNAME = 'hemantdevgan'
        FRONTEND_IMAGE_NAME = 'my-frontend'
        FRONTEND_IMAGE_VERSION = 'v1.0.0'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                sh 'git checkout main'
                sh 'git pull origin main'
                sh 'pwd'
                echo 'checked'
            }
        }
        stage('Build') {
            steps {
                echo 'Building images...'
                sh "docker build -f frontend/Dockerfile -t ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_VERSION} ."

            }
        }
        stage('docker login') {
            steps {
                echo 'Logging into Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    echo 'Logged in successfully!'
                }
            }
        }
        stage('Push') {
            steps {
                echo 'Pushing images...'
                sh "docker tag ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_VERSION} ${DOCKER_USERNAME}/${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_VERSION}"
                sh "docker push ${DOCKER_USERNAME}/${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_VERSION}"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'

            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                sh 'kubectl apply -f k8s/frontend.yml'
            }
        }
    }
}