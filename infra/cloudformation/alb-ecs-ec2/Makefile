include ../utils/StopStart.mk

stop:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	echo "Sets 0 as desired count of service $(PROJECT_NAME)-$${DEPLOYMENT_NAME}" && \
	aws ecs update-service --service $(PROJECT_NAME)-$${DEPLOYMENT_NAME} --desired-count 0 || true && \
	aws ecs wait services-stable --services $(PROJECT_NAME)-$${DEPLOYMENT_NAME} || true && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd --profile cicd
