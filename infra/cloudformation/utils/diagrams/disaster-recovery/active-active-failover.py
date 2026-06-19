from diagrams import Cluster, Diagram, Edge
from diagrams.aws.compute import EC2ContainerRegistry
from diagrams.aws.compute import ECS
from diagrams.aws.general import InternetAlt1
from diagrams.aws.network import ELB
from diagrams.aws.network import Route53

with Diagram("Active - Active\nDisaster Recovery strategy\nwith ALB serving ECS Fargate tasks") as diag:
    internetAlt1 = InternetAlt1("sandbox.theatomicity.com")
    dns = Route53("Alias\nRecordSetGroup\n")
    internetAlt1 >> dns
    with Cluster("eu-west-1"):
        lb1 = ELB("ALB1")
        ecr1 = EC2ContainerRegistry("ECR1")
        with Cluster("ECS Service"):
            svc_group1 = [ECS("ECS Task")]
        svc_group1 << ecr1
    with Cluster("eu-west-3"):
        lb2 = ELB("ALB2")
        ecr2 = EC2ContainerRegistry("ECR2")
        with Cluster("ECS Service"):
            svc_group2 = [ECS("ECS Task")]
        svc_group2 << ecr2
    dns >> Edge(color="darkblue", label="Failover: PRIMARY\n(with HealthCheck)") >> lb1
    dns >> Edge(color="darkblue", label="Failover: SECONDARY") >> lb2
    lb1 >> svc_group1
    lb2 >> svc_group2
diag
