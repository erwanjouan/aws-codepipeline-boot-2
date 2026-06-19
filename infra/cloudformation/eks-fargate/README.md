# Spring Boot deployment on EKS Fargate

## Usage

```shell
make start
```

- Uses [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/)
- Load balancer endpoint is
    - created at runtime
    - not in Cloudformation template outputs

## Service accounts / IAM Role

- Pods are created with ```aws-load-balancer-controller``` service account in template

- To bind IAM and K8S service accounts, a role (```AmazonEKSLoadBalancerControllerRole```) is created with trust policy
  that refers to ```aws-load-balancer-controller```

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::467420073914:oidc-provider/oidc.eks.eu-west-1.amazonaws.com/id/2A645CD38789983694A45080CF1983C3"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "oidc.eks.eu-west-1.amazonaws.com/id/2A645CD38789983694A45080CF1983C3:aud": "sts.amazonaws.com",
          "oidc.eks.eu-west-1.amazonaws.com/id/2A645CD38789983694A45080CF1983C3:sub": "system:serviceaccount:aws-codepipeline-boot-eks-fargate:aws-load-balancer-controller"
        }
      }
    }
  ]
}
```

## References

The easy way with eksctl

https://docs.aws.amazon.com/eks/latest/userguide/getting-started-eksctl.html

https://docs.aws.amazon.com/eks/latest/userguide/getting-started-console.html

https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-eks-fargateprofile.html

### IAM roles for service accounts

https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html`

### A Container-Free Way to Configure Kubernetes Using AWS Lambda

https://aws.amazon.com/fr/blogs/opensource/a-container-free-way-to-configure-kubernetes-using-aws-lambda/

Helm requires disk temporary folders, will only work on /tmp in lambda.

### EKS Cluster Access

By default, only the user/role that created the cluster can have access to it.

https://repost.aws/knowledge-center/amazon-eks-cluster-access

It can be problematic if EKS Cluster was created in CopePipeline with a specific role.
In this case, the role must be assumed by the local user.

- Go to CloudTrail to get the ```userIdentity``` used for creating the EKS
  Cluster (```CreateCluster``` [event](./createCluster.json)).
- Set the keys provided by ```aws sts assume-role ...``` of the role that created the cluster in environment variables.
- Call ```kubectl```

### Install the AWS Load Balancer Controller

https://repost.aws/knowledge-center/eks-alb-ingress-controller-fargate

- Make a different service account linked to different role for initial pod

[AWS Load Balancer Controller documentation](https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/)

### KubeCtl

Restart all pods in kube-system

```
kubectl -n kube-system rollout restart deploy
```

Connect to pod

```
kubectl exec -n aws-codepipeline-boot-eks-fargate --stdin --tty deployment-test -- /bin/sh
```

### KubeConfig

Kubernetes java client doesn't know about $PATH for aws cli, it needs to be set manually.
https://stackoverflow.com/a/71222634

Java arg
https://stackoverflow.com/a/61143833

### Custom resource

replay a custom resource -> test the event in lambda console

### Logging

- [Fargate logging](https://docs.aws.amazon.com/eks/latest/userguide/fargate-logging.html)

### Scaling

- [Horizontal Pod Autoscaler](https://docs.aws.amazon.com/eks/latest/userguide/horizontal-pod-autoscaler.html)
- Metrics server needs to be installed
    - [EKS WorkShop: Deploy the Metrics Server](https://archive.eksworkshop.com/beginner/080_scaling/deploy_hpa/#deploy-the-metrics-server)
    - [Horizontal Pod Autoscaler(hpa) — Know Everything About it](https://foxutech.medium.com/horizontal-pod-autoscaler-hpa-know-everything-about-it-5637c7d2438a)

### Supported SDK

- IAM roles for Services account
- [Using a supported AWS SDK](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts-minimum-sdk.html)