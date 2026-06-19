PROJECT_NAME:=aws-codepipeline-boot

start:
	cfn-lint macro.yml && \
	export DEPLOYMENT_NAME=$$(../folder.sh) && \
	aws cloudformation deploy \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME} \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file macro.yml \
		--parameter-overrides \
			ProjectName=$(PROJECT_NAME) \
			DeploymentName=$${DEPLOYMENT_NAME} \
			ProjectDeploymentName=$(PROJECT_NAME)-$${DEPLOYMENT_NAME}

stop:
	export DEPLOYMENT_NAME=$$(../folder.sh) && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}

