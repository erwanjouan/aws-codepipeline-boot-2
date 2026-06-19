deploy:
	export $$(cat .env | xargs) && cdk synth --debug true \
            fargate-app \
            --exclusively