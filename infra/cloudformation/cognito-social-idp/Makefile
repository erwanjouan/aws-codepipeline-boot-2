include ../utils/StopStart.mk

COGNITO_DOMAIN:=the-atomicity.auth.eu-west-1.amazoncognito.com
COGNITO_CLIENT_ID:=4hj1rljiu4e6jpdk8sqb385h4t
COGNITO_CLIENT_SECRET:=i8e0l8founu2jjehbht3aoil2ujok9tugu6ajervgiadsk4mphb
GOOGLE_CLIENT_SECRET:=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

idp:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	aws cloudformation deploy \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file idp.yml \
		--stack-name $(PROJECT_NAME)-$${DEPLOYMENT_NAME}-idp \
		--parameter-overrides \
			ClientSecret=$(GOOGLE_CLIENT_SECRET) \
			ProjectDeploymentName=$(PROJECT_NAME)-$${DEPLOYMENT_NAME}

test:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	RAW_REDIRECT_URI=$$(echo "http://localhost:8080/login/oauth2/code/cognito") && \
	TEST_URI=$$(echo "https://$(COGNITO_DOMAIN)/oauth2/authorize?client_id=$(COGNITO_CLIENT_ID)&redirect_uri=$${RAW_REDIRECT_URI}&scope=openid&response_type=code&state=STATE") && \
	echo $$TEST_URI && \
	curl -L $$TEST_URI

run:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	export COGNITO_USER_POOL_ID=$$(aws cognito-idp list-user-pools --max-results 10 --query 'UserPools[?contains(Name,`CognitoUserPool`)==`true`].Id' --output text) && \
	export COGNITO_CLIENT_ID=$$(aws cognito-idp list-user-pool-clients --user-pool-id $${COGNITO_USER_POOL_ID} --query "UserPoolClients[0].ClientId" --output text) && \
	export COGNITO_CLIENT_SECRET=$(COGNITO_CLIENT_SECRET) && \
	export PROJECT_DEPLOYMENT_NAME=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} && \
	export REGION=eu-west-1 && \
	export REDIRECT_URI=http://localhost:8080 && \
	export SPRING_PROFILES_ACTIVE=$${DEPLOYMENT_NAME} && \
	cd .. && mvn spring-boot:run

call:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
	export COGNITO_USER_POOL_ID=$$(aws cognito-idp list-user-pools --max-results 10 --query 'UserPools[?contains(Name,`CognitoUserPool`)==`true`].Id' --output text) && \
	export COGNITO_CLIENT_ID=$$(aws cognito-idp list-user-pool-clients --user-pool-id $${COGNITO_USER_POOL_ID} --query "UserPoolClients[0].ClientId" --output text) && \
	export PROJECT_DEPLOYMENT_NAME=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} && \
	export REDIRECT_URI=http://localhost:8080 && \
	curl -vvv -L https://the-atomicity.auth.eu-west-1.amazoncognito.com/oauth2/authorize?response_type=code&client_id=$${COGNITO_CLIENT_ID}&scope=openid&state=_QehYbEeYhP8r7HJ4kMnXeYUe9iaaIsKT3swHBbV7Po%3D&redirect_uri=$${REDIRECT_URI}/login/oauth2/code/cognito
