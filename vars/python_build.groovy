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
        stage('Building') {
            steps {
                sh 'ls'
                sh 'pwd'
                sh 'ls reciever/'
                withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                    sh "docker login -u 'jovan9876' -p '$TOKEN' docker.io"
                    sh "docker build -t reciever:latest -f reciever/Dockerfile ."
                    sh "docker push jovan9876/reciever:reciever"
                    sh "docker build -t storage:latest -f storage/Dockerfile ."
                    sh "docker push jovan9876/storage:storage"
                    sh "docker build -t processing:latest -f processing/Dockerfile ."
                    sh "docker push jovan9876/processing:processing"
                    sh "docker build -t audit_log:latest -f audit_log/Dockerfile ."
                    sh "docker push jovan9876/audit_log:audit_log"
                }
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

