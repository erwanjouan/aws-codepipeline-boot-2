include ../utils/StopStart.mk

deploy:
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
        printf 'https://%s.console.aws.amazon.com/codesuite/codedeploy/deployments/%s\n' $(REGION) $${DEPLOYMENT_ID} ; \
	fi

dynamodb-local:
	mkdir .dynamodb || true && \
		docker-compose up

list-tables:
	 aws dynamodb --endpoint-url http://localhost:8000 list-tables

create-table:
	 aws dynamodb --endpoint-url http://localhost:8000 create-table \
		--table-name DopC02Domains \
		--provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
		--attribute-definitions \
			AttributeName=Name,AttributeType=S AttributeName=Id,AttributeType=S \
		--key-schema \
			AttributeName=Name,KeyType=HASH AttributeName=Id,KeyType=RANGE

delete-table:
	aws dynamodb --endpoint-url http://localhost:8000 delete-table \
	--table-name Domains

describe-table:
	aws dynamodb --endpoint-url http://localhost:8000 describe-table \
	--table-name Domains

scan:
	aws dynamodb --endpoint-url http://localhost:8000 scan \
	--table-name Domains

upload:
	for i in $$(ls .dynamodb.*.json); do \
		echo $$i ; \
	done
