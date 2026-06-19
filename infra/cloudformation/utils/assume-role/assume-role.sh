#!/bin/bash

TARGET_ROLE_ARN=arn:aws:iam::163822821219:role/codecommit-codepipeline
TARGET_ROLE_NAME=codecommit-codepipeline

ASSUME_ROLE=$(aws sts assume-role --role-arn $TARGET_ROLE_ARN --role-session-name $TARGET_ROLE_NAME --profile codepipeline)
export AWS_ACCESS_KEY_ID=$(echo $ASSUME_ROLE | jq -r '.Credentials.AccessKeyId')
export AWS_SECRET_ACCESS_KEY=$(echo $ASSUME_ROLE | jq -r '.Credentials.SecretAccessKey')
export AWS_SESSION_TOKEN=$(echo $ASSUME_ROLE | jq -r '.Credentials.SessionToken')
aws sts get-caller-identity

TARGET_REPO_NAME=aws-codepipeline-boot

aws codecommit get-repository --repository-name $TARGET_REPO_NAME

aws s3 cp test.txt s3://aws-codepipeline-boot-api-gw--artifactoutputbucket-r5pa4k030etq