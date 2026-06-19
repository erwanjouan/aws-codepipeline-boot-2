TARGET_REGION:=eu-west-1

install:
	brew install cloudformation-cli

# initialize the folder to hold the module. When you run init, you can now pick between a resource or a module.
# When you choose a name for your module, be sure to use one that isn’t reserved. The name must end with ::MODULE.

# When this command is complete, it creates the following:
#	A fragments folder that contains the CloudFormation template you are going to use for the module.
#	An .rpdk-config file that contains details about the module, including its name.
#	An rpdk.log that contains logs from running cfn commands.

init:
	cfn init -v -t $(MODULE_ID) -a MODULE

submit:
	cfn submit -v --region $(TARGET_REGION) --profile $(AWS_CLI_PROFILE)

list:
	aws cloudformation list-type-versions --region $(TARGET_REGION) --type MODULE --type-name $(MODULE_ID) --profile $(AWS_CLI_PROFILE)

default-version:
	aws cloudformation set-type-default-version --region $(TARGET_REGION) --type MODULE --type-name $(MODULE_ID) --version-id $(DEFAULT_VERSION) --profile $(AWS_CLI_PROFILE)

delete:
	aws cloudformation deregister-type --region $(TARGET_REGION)  --type MODULE --type-name $(MODULE_ID) --profile $(AWS_CLI_PROFILE) #--version-id $(TARGET_VERSION)

check:
	aws cloudformation describe-type --region $(TARGET_REGION)  --type MODULE --type-name $(MODULE_ID) --profile $(AWS_CLI_PROFILE)
