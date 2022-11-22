def call() {
    pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'pip install -r ./audit_log/requirements.txt'
                sh 'pip install -r ./health/requirements.txt'
                sh 'pip install -r ./processing/requirements.txt'
                sh 'pip install -r ./reciever/requirements.txt'
                sh 'pip install -r ./storage/requirements.txt'
            }
        }
        stage('Static Code Checking') {
            steps {
                script {
                    sh 'pylint-fail-under --fail_under 5.0 **/*.py'
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
  }
}

