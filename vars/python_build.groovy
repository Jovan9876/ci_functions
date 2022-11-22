def call() {
    pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'ls'
            }
        }
        stage('Python Lint'){
            steps {
                sh 'pylint-fail-under --fail_under 5.0 **.py'
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
