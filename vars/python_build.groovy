def call(dockerRepoName, imageName) {
    pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh "pip install -r ./${dockerRepoName}/requirements.txt"
            }
        }
        stage('Static Code Checking') {
            steps {
                script {
                    sh "pylint-fail-under --fail_under 5.0 ${dockerRepoName}/*.py"
                }
            }
        }
        stage('Building') {
            steps {
                withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                    sh "docker login -u 'jovan9876' -p '$TOKEN' docker.io"
                    sh "docker build -t ${dockerRepoName}:latest -f ${dockerRepoName}.Dockerfile --tag jovan9876/${dockerRepoName}:${imageName} ."
                    sh "docker push jovan9876/${dockerRepoName}:${imageName}"
                }
            }
        }
        stage('Zip Artifacts') {
            steps {
                sh "zip -r ${dockerRepoName}.zip ${dockerRepoName}/"
            }
            post {
                always {
                    archiveArtifacts "${dockerRepoName}.zip"
                } 
            }
        }
//         stage('Deploy') {
//             steps {
//                 sshagent(credentials: ['ACIT-3855-keys']) {
//                     sh "ssh -o StrictHostKeyChecking=no -l azureuser acit-3855.eastus.cloudapp.azure.com docker pull jovan9876/${dockerRepoName}"
//                     sh "ssh -o StrictHostKeyChecking=no -l azureuser acit-3855.eastus.cloudapp.azure.com docker-compose -f ACIT3855/deployment/docker-compose.yml up -d --build"
//                 }
//             }   
//         }
    }
    }
}

