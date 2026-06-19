include ../utils/StopStart.mk

stress:
	aws ssm send-command --cli-input-json file://ssm-stress.json

stop:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-ami && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy