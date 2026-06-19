sam-init:
	sam init \
		--no-interactive \
		--name sam-lambda-jar \
		--location . \
		--architecture x86_64 \
		--runtime java17 \
		--dependency-manager maven \
		--tracing \
		--application-insights \
		--output-dir sam-lambda-jar \
		--debug

sam-build:
	 cd sam-lambda-jar && sam build

sam-local:
	 cd sam-lambda-jar && sam local invoke
