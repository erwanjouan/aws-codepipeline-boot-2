include ../utils/StopStart.mk

enable-saved-config:
	aws elasticbeanstalk update-environment \
		--environment-name $(PROJECT_NAME)-$(DEPLOYMENT_NAME) \
		--option-settings file://eb/saved-config.json

discard-saved-config:
	aws elasticbeanstalk update-environment \
		--environment-name $(PROJECT_NAME)-$(DEPLOYMENT_NAME) \
		--options-to-remove \
			Namespace=aws:autoscaling:asg,OptionName=MinSize \
			Namespace=aws:autoscaling:asg,OptionName=MaxSize \
			Namespace=aws:autoscaling:updatepolicy:rollingupdate,OptionName=RollingUpdateEnabled
