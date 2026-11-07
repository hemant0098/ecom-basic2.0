pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                sh 'git checkout main'
                sh 'git pull origin main'
            }
        }
        stage('Build') {
            steps {
                echo 'Building...'

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
            }
        }
    }
}