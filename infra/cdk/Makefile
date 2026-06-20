deploy:
	export $$(cat .env | xargs) && cdk synth \
            --app "npx ts-node --prefer-ts-exts bin/$${DEPLOYMENT_NAME}/cdk.ts" \
            --debug true \
            $${DEPLOYMENT_NAME} \
            --exclusively