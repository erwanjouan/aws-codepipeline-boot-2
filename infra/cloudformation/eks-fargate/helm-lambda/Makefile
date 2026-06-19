IMAGE_NAME:=helm-lambda
IMAGE_TAG:=test
PROJECT_DEPLOYMENT_NAME:=aws-codepipeline-boot-eks-fargate
DEPLOYMENT_NAME:=eks-fargate
ACCOUNT_ID:=467420073914

build:
	export PROJECT_DEPLOYMENT_NAME=$(PROJECT_DEPLOYMENT_NAME) && \
	export K8S_NAMESPACE=$(PROJECT_DEPLOYMENT_NAME) && \
	docker build --platform linux/amd64 -t $(IMAGE_NAME):$(IMAGE_TAG) .

run:
	docker run --platform linux/amd64 \
		-p 9000:8080 \
		-v $$HOME/.aws:/root/.aws \
		-e AMAZON_EKS_LOAD_BALANCER_CONTROLLER_ROLE_ARN="arn:aws:iam::467420073914:role/AmazonEKSLoadBalancerControllerRole" \
		-e PROJECT_DEPLOYMENT_NAME=$(PROJECT_DEPLOYMENT_NAME) \
		-e DEPLOYMENT_NAME=$(DEPLOYMENT_NAME) \
		-e K8S_NAMESPACE=$(PROJECT_DEPLOYMENT_NAME) \
		-e AWS_DEFAULT_REGION=eu-west-1 \
		-e IMAGE_TAG=dad5a8bb33cdd71b52bb1f408c357b690b89a52c \
		-e NUMBER_OF_REPLICAS=3 \
		-e AWS_ACCOUNT_ID=$(ACCOUNT_ID) \
		-e AWS_PROFILE=cf \
		$(IMAGE_NAME):$(IMAGE_TAG)

rebuild:
	make build && make run

test_create:
	curl "http://localhost:9000/2015-03-31/functions/function/invocations" \
	 -d '@events/create_event.json'

test_update:
	curl "http://localhost:9000/2015-03-31/functions/function/invocations" \
	 -d '@events/update_event.json'

test_delete:
	curl "http://localhost:9000/2015-03-31/functions/function/invocations" \
	 -d '@events/delete_event.json'

invoke_create:
	aws lambda invoke \
		--function-name aws-codepipeline-boot-eks-depl-EksConfigureLambda2-eiiQEJKU422s \
		 --cli-binary-format raw-in-base64-out \
		--payload file://events/create_event.json \
		.output.json

invoke_update:
	aws lambda invoke \
		--function-name aws-codepipeline-boot-eks-depl-EksConfigureLambda2-UzDxVPVcEAKV \
		 --cli-binary-format raw-in-base64-out \
		--payload file://events/update_event.json \
		.output.json

complete_custom:
	curl -H 'Content-Type: ''' -X PUT -d '{ \
		"Status": "SUCCESS", \
		"PhysicalResourceId": "CustomResourcePhysicalID", \
		"StackId": "arn:aws:cloudformation:eu-west-1:467420073914:stack/aws-codepipeline-boot-eks-fargate-deploy/1ad638a0-aa4b-11ee-a571-02d5c84b42bb", \
		"RequestId": "ba26b710-4a13-4e55-bda4-2cd6cd5237f8", \
		"LogicalResourceId": "CustomResourceEksConfigure" \
	  }' 'https://cloudformation-custom-resource-response-euwest1.s3-eu-west-1.amazonaws.com/arn%3Aaws%3Acloudformation%3Aeu-west-1%3A467420073914%3Astack/aws-codepipeline-boot-eks-fargate-deploy/....'

delete_ingress:
		export PROJECT_DEPLOYMENT_NAME=$(PROJECT_DEPLOYMENT_NAME) && \
    	export K8S_NAMESPACE=$(PROJECT_DEPLOYMENT_NAME) && \
    	cat k8s/04_ingress_service.yml | envsubst | \
    	kubectl delete -f -
