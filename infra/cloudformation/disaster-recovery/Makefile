include ../utils/StopStart.mk

stackset-role:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation deploy \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-stackset-role \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file stackset-role.yml \
		--parameter-overrides \
			ProjectName=$(PROJECT_NAME) \
			DeploymentName=$${DEPLOYMENT_NAME} \
			ProjectDeploymentName=$(PROJECT_NAME)-$${DEPLOYMENT_NAME}

stop:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-cicd && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy --region eu-west-3 && \
	aws cloudformation delete-stack --stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-route53

create-route53:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	PRIMARY_ALB_DNS_NAME=$$(aws cloudformation describe-stacks \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
		--query 'Stacks[0].Outputs[?OutputKey==`ApplicationLoadBalancerDNSName`].OutputValue' \
		--output text) && \
	PRIMARY_ALB_CANONICAL_HOSTED_ZONEID=$$(aws cloudformation describe-stacks \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
		--query 'Stacks[0].Outputs[?OutputKey==`ApplicationLoadBalancerCanonicalHostedZoneID`].OutputValue' \
		--output text) && \
	SECONDARY_ALB_DNS_NAME=$$(aws cloudformation describe-stacks \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
		--region eu-west-3 \
		--query 'Stacks[0].Outputs[?OutputKey==`ApplicationLoadBalancerDNSName`].OutputValue' \
		--output text) && \
	SECONDARY_ALB_CANONICAL_HOSTED_ZONEID=$$(aws cloudformation describe-stacks \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-deploy \
		--region eu-west-3 \
		--query 'Stacks[0].Outputs[?OutputKey==`ApplicationLoadBalancerCanonicalHostedZoneID`].OutputValue' \
		--output text) && \
	aws cloudformation deploy \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-route53 \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file route53.yml \
		--parameter-overrides \
			PrimaryAlbDNSName=$${PRIMARY_ALB_DNS_NAME} \
			PrimaryAlbCanonicalHostedZoneID=$${PRIMARY_ALB_CANONICAL_HOSTED_ZONEID} \
			SecondaryAlbDNSName=$${SECONDARY_ALB_DNS_NAME} \
			SecondaryAlbCanonicalHostedZoneID=$${SECONDARY_ALB_CANONICAL_HOSTED_ZONEID}
