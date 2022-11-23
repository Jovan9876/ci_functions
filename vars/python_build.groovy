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
                withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                    sh "docker login -u '<username>' -p '$TOKEN' docker.io"
                    sh "docker build -t reciever:latest -f reciever/ --tag reciever/reciever:reciever ."
                    sh "docker push jovan9876/reciever:reciever"
                    sh "docker build -t storage:latest -f storage/ --tag storage/storage:storage ."
                    sh "docker push jovan9876/storage:storage"
                    sh "docker build -t processing:latest -f processing/ --tag processing/processing:processing ."
                    sh "docker push jovan9876/processing:processing"
                    sh "docker build -t audit_log:latest -f audit_log/ --tag audit_log/audit_log:audit_log ."
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

