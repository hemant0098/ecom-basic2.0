pipeline {
    agent any


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
                sh 'docker build -f frontend/Dockerfile -t my-frontend:v1.0 .'

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
                sh 'docker tag my-frontend:v1.0 hemantdevgan/my-frontend:v1.0'
                sh 'docker push my-frontend:v1.0'
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