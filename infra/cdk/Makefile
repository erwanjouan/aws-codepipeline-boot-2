deploy:
	export $$(cat .env | xargs) && cdk synth \
            --app "npx ts-node --prefer-ts-exts bin/alb-ecs-fargate/cdk.ts" \
            --debug true \
            fargate-cicd \
            --exclusively