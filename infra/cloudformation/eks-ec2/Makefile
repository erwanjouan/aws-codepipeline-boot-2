include ../utils/StopStart.mk

connect:
	@export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
    export K8S_NAMESPACE=$(PROJECT_NAME)-$${DEPLOYMENT_NAME} && \
	make kubeconfig && \
    POD=$$(kubectl get pod -n $${K8S_NAMESPACE} -l app.kubernetes.io/name=app-$${K8S_NAMESPACE} -o jsonpath="{.items[0].metadata.name}") && \
    echo "Connecting to namespace=[$${K8S_NAMESPACE}], pod=($${POD})" && \
	kubectl exec -n $${K8S_NAMESPACE} --stdin --tty $${POD} -- /bin/bash

access:
	@make kubeconfig && ../utils/eks/patch_eks_auth.sh

kubeconfig:
	export DEPLOYMENT_NAME=$$(../utils/folder.sh) && \
    aws eks --region eu-west-1 update-kubeconfig --name $(PROJECT_NAME)-$${DEPLOYMENT_NAME} --profile cf && \
	sed -i .bak -e "/command: aws/s?aws?$$(which aws)?" ~/.kube/config

local-lambda:
	cd lambda && \
	pip3 install -r requirements.txt --target ./package && \
	cp function.py ./package/function.py

run-local-lambda:
	export IMAGE_TAG=$$(aws ecr describe-images --repository-name aws-codepipeline-boot-eks-ec2 \
							--query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]' --output text) && \
	AWS_DEFAULT_REGION=eu-west-1 \
	CLUSTER_NAME=aws-codepipeline-boot-eks-ec2 \
	K8S_USER_NAMESPACE=aws-codepipeline-boot-eks-ec2 \
	AMAZON_EKS_LOAD_BALANCER_CONTROLLER_ROLE_ARN=arn:aws:iam::467420073914:role/AmazonEKSLoadBalancerControllerRole \
	python3 ./lambda/package/function.py

eksctl-deploy:
	eksctl create cluster --name $(CLUSTER_NAME) --region $(REGION) --ec2

eksctl-destroy:
	eksctl delete cluster --name $(CLUSTER_NAME) --region $(REGION)
