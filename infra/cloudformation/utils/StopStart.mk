PROJECT_NAME:=aws-codepipeline-boot
WORKLOAD_ACCOUNT_ID:=467420073914

start:
	DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	CROSS_ACCOUNT_ROLE_ARN=$$(aws cloudformation describe-stacks \
    		--stack-name $(PROJECT_NAME)-pre-requisites \
    		--query 'Stacks[0].Outputs[?OutputKey==`CrossAccountRoleArn`].OutputValue' \
    		--output text) && \
    CLOUDFORMATION_ROLE_ARN=$$(aws cloudformation describe-stacks \
        		--stack-name $(PROJECT_NAME)-pre-requisites \
        		--query 'Stacks[0].Outputs[?OutputKey==`CloudFormationRoleArn`].OutputValue' \
        		--output text) && \
	aws cloudformation deploy \
		--profile cicd \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file cicd.yml \
		--parameter-overrides \
			ProjectName=$(PROJECT_NAME) \
			DeploymentName=$${DEPLOYMENT_NAME} \
			ProjectDeploymentName=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} \
			WorkloadAccountId=$(WORKLOAD_ACCOUNT_ID) \
			CrossAccountRoleArn=$${CROSS_ACCOUNT_ROLE_ARN} \
			CloudFormationRoleArn=$${CLOUDFORMATION_ROLE_ARN}

stop:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy && \
	aws cloudformation delete-stack --profile cicd --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd

notebook:
	jupyter nbconvert --clear-output --inplace *.ipynb && \
	jupyter notebook *.ipynb

watch-service:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation describe-stacks \
        --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
        --query 'Stacks[0].Outputs[?OutputKey==`WatchServiceEndpoint`].OutputValue' --output text

url:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	DNS_NAME=$$(aws elbv2 describe-load-balancers --query "LoadBalancers[0].DNSName" --output text) && \
	printf 'http://%s/%s\n' $${DNS_NAME} $${DEPLOYMENT_NAME}

validate:
	cfn-lint cicd.yml  && \
	cfn-lint infra.yml

deploy:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation deploy \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
		--capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
		--template-file infra.yml \
		--parameter-overrides \
			ProjectName=$(PROJECT_NAME) \
			ProjectVersion=5b694da2ba559c43dda8ab6eb4c8189f9997a584 \
			DeploymentName=$${DEPLOYMENT_NAME} \
			ProjectDeploymentName=$(PROJECT_NAME)-$${DEPLOYMENT_NAME}
