include ../utils/StopStart.mk

codedeploy:
	@DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	export PROJECT_DEPLOYMENT_NAME=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} && \
	export PUBLISHED_VERSION=$$(aws lambda publish-version --function-name $${PROJECT_DEPLOYMENT_NAME} --query "Version" --output text) && \
	LAMBDA_ALIAS_NAME=$$(aws cloudformation describe-stacks --stack-name $${PROJECT_DEPLOYMENT_NAME}-deploy --query 'Stacks[0].Outputs[?OutputKey==`LambdaAlias`].OutputValue' --output text) && \
	export VERSION_ON_ALIAS=$$(aws lambda get-alias --name $${LAMBDA_ALIAS_NAME} --function-name $${PROJECT_DEPLOYMENT_NAME} --query "FunctionVersion" --output text) && \
	if [ "$${VERSION_ON_ALIAS}" = "$${PUBLISHED_VERSION}" ]; then \
		printf "%s:%s matches last published version %s, nothing to do\n" $${LAMBDA_ALIAS_NAME} $${VERSION_ON_ALIAS} $${PUBLISHED_VERSION}; \
	else \
		printf "%s:%s New version detected %s for Lambda, triggering CodeDeploy\n" $${LAMBDA_ALIAS_NAME} $${VERSION_ON_ALIAS} $${PUBLISHED_VERSION} ; \
		PREVIOUS_REVISION=$$(aws lambda list-versions-by-function --function-name $${PROJECT_DEPLOYMENT_NAME} --query "Versions[-2].Version" --output text) && \
        envsubst < deployment.yml > .deployment.yml && \
        DEPLOYMENT_ID=$$(aws deploy create-deployment --cli-input-yaml file://.deployment.yml --query "deploymentId" --output text) && \
        printf 'Created CodeDeploy deployment from .deployment.yml file see. https://%s.console.aws.amazon.com/codesuite/codedeploy/deployments/%s\n' $(REGION) $${DEPLOYMENT_ID} ; \
	fi


canary:
	DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
    PROJECT_DEPLOYMENT_NAME=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} && \
	REST_API_ID=$$(aws cloudformation describe-stacks --stack-name $${PROJECT_DEPLOYMENT_NAME}-deploy --query 'Stacks[0].Outputs[?OutputKey==`RestApiId`].OutputValue' --output text) && \
    LAMBDA_ARN=$$(aws cloudformation describe-stacks --stack-name $${PROJECT_DEPLOYMENT_NAME}-deploy --query 'Stacks[0].Outputs[?OutputKey==`LambdaArn`].OutputValue' --output text) && \
    ACCOUNT_ID=$$(aws sts get-caller-identity --query "Account" --output text) && \
    DEFAULT_REGION=$$(aws configure get region) && \
    CANARY_ALIAS=green && \
    STAGE_NAME=dev && \
    aws lambda add-permission \
	    --function-name $${LAMBDA_ARN}:$${CANARY_ALIAS} \
    	--statement-id permission \
	    --action lambda:InvokeFunction \
	  	--principal apigateway.amazonaws.com \
	  	--source-arn arn:aws:execute-api:$${DEFAULT_REGION}:$${ACCOUNT_ID}:$${REST_API_ID}/*/*/* || true && \
	aws apigateway create-deployment  --rest-api-id $${REST_API_ID} \
        --canary-settings percentTraffic=50,stageVariableOverrides={lambdaAlias=$${CANARY_ALIAS}},useStageCache=false \
        --stage-name $${STAGE_NAME} && \
    aws apigateway get-stage --rest-api-id $${REST_API_ID} --stage-name $${STAGE_NAME}
