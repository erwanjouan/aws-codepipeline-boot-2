include ../utils/StopStart.mk

get:
	ACCOUNT_ID=$$(aws sts get-caller-identity --query Account --output text) && \
	ASSET_NAME=$$(aws codeartifact list-package-version-assets \
                  		--domain maven \
                  		--domain-owner $$ACCOUNT_ID \
                  		--repository private \
                  	    --format maven \
                  	    --namespace com.the.atomicity \
                  	    --package aws-codepipeline-boot \
                  	    --package-version 0.0.1-SNAPSHOT \
                  	    --query 'assets[?ends_with(name, `.jar`)==`true`].name' \
                  	    --output text) && \
    echo ASSET_NAME $$ASSET_NAME && \
	aws codeartifact get-package-version-asset \
		--domain maven \
		--domain-owner $$ACCOUNT_ID \
		--repository private \
	    --format maven \
	    --namespace com.the.atomicity \
	    --package aws-codepipeline-boot \
	    --package-version 0.0.1-SNAPSHOT \
	    --asset $$ASSET_NAME \
	    app.jar

list:
	ACCOUNT_ID=$$(aws sts get-caller-identity --query Account --output text) && \
	aws codeartifact list-package-version-assets \
		--domain maven \
		--domain-owner $$ACCOUNT_ID \
		--repository private \
	    --format maven \
	    --namespace com.the.atomicity \
	    --package aws-codepipeline-boot \
	    --package-version 0.0.1-SNAPSHOT \
	    --query 'assets[?ends_with(name, `.jar`)==`true`].name' \
	    --output text