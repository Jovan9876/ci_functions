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
                    sh "docker login -u 'jovan9876' -p '$TOKEN' docker.io"
                    sh "docker build -t reciever:latest -f reciever.Dockerfile --tag jovan9876/reciever:latest ."
                    sh "docker push jovan9876/reciever:latest"
                    sh "docker build -t storage:latest -f storage.Dockerfile --tag jovan9876/storage:latest ."
                    sh "docker push jovan9876/storage:latest"
                    sh "docker build -t processing:latest -f processing.Dockerfile --tag jovan9876/processing:latest ."
                    sh "docker push jovan9876/processing:latest"
                    sh "docker build -t audit_log:latest -f audit_log.Dockerfile --tag jovan9876/audit_log:latest ."
                    sh "docker push jovan9876/audit_log:latest"
                }
            }
        }
        stage('Zip Artifacts') {
            steps {
                sh 'zip -r reciever.zip reciever/'
                sh 'zip -r storage.zip storage/'
                sh 'zip -r processing.zip processing/'
                sh 'zip -r audit_log.zip audit_log/'
            }
            post {
                always {
                    archiveArtifacts 'reciever.zip'
                    archiveArtifacts 'storage.zip'
                    archiveArtifacts 'processing.zip'
                    archiveArtifacts 'audit_log.zip'
                } 
            }
        }
        stage('Deploy') {
            steps {
                sshagent(credentials: ['ACIT-3855-keys']) {
                    sh 'ssh -o StrictHostKeyChecking=no -l azureuser acit-3855.eastus.cloudapp.azure.com docker pull jovan9876/reciever && docker-compose -f ACIT3855/deployment/docker-compose.yml up -d'
//                     :latest;docker pull jovan9876/storage:latest;docker pull jovan9876/processing:latest;docker pull jovan9876/audit_log:latest;ACIT3855/deployment/docker-compose.yml up -d'
                }
            }   
        }
    }
    }
}

