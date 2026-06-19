include ../utils/StopStart.mk

stop:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-eb-single-docker-deploy && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-eb-multi-docker-deploy