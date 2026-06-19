#!/bin/sh
export APPLICABLE_REGION=$1
aws ecr get-login-password --region ${APPLICABLE_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${APPLICABLE_REGION}.amazonaws.com
docker build -f ${DEPLOYMENT_NAME}/Dockerfile -t ${PROJECT_DEPLOYMENT_NAME} .
docker tag ${PROJECT_DEPLOYMENT_NAME}:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${APPLICABLE_REGION}.amazonaws.com/${PROJECT_DEPLOYMENT_NAME}:latest
docker tag ${PROJECT_DEPLOYMENT_NAME}:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${APPLICABLE_REGION}.amazonaws.com/${PROJECT_DEPLOYMENT_NAME}:${CODEBUILD_RESOLVED_SOURCE_VERSION}
docker push --all-tags ${AWS_ACCOUNT_ID}.dkr.ecr.${APPLICABLE_REGION}.amazonaws.com/${PROJECT_DEPLOYMENT_NAME}
