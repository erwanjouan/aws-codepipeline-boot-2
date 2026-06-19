include ../StopStart.mk

prereq:
	aws cloudformation deploy \
		--stack-name $(PROJECT_NAME)-pre-requisites \
		--capabilities CAPABILITY_NAMED_IAM \
		--template-file prereq.yml \
		--parameter-overrides \
			ProjectName=$(PROJECT_NAME)
