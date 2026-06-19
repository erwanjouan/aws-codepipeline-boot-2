from diagrams import Diagram, Cluster, Edge
from diagrams.aws.compute import EC2ElasticIpAddress
from diagrams.aws.general import InternetAlt1
from diagrams.aws.network import NATGateway, InternetGateway, PublicSubnet, RouteTable, PrivateSubnet

with Diagram("Base Network", direction="TB") as diag:
    internetAlt1 = InternetAlt1("Internet")
    with Cluster("VPC: 10.0.0.0/16"):
        internetGateway = InternetGateway("InternetGateway")
        with Cluster("eu-west-1a"):
            with Cluster("PublicSubnet1: 10.0.3.0/24"):
                natGateway = NATGateway("NATGateway")
                natPublicIP = EC2ElasticIpAddress("NatPublicIP")
                natGateway >> Edge(color="darkblue", style="dashed") >> natPublicIP
                publicSubnet1 = PublicSubnet("PublicSubnet1")
            with Cluster("PrivateSubnet1: 10.0.0.0/24"):
                privateSubnet1 = PrivateSubnet("PrivateSubnet1")
        with Cluster("eu-west-1b"):
            with Cluster("PublicSubnet2: 10.0.4.0/24"):
                publicSubnet2 = PublicSubnet("PublicSubnet2")
            with Cluster("PrivateSubnet2: 10.0.1.0/24"):
                privateSubnet2 = PrivateSubnet("PrivateSubnet2")

        # Private / NAT
        privateRouteTable = RouteTable("PrivateRouteTable")
        privateSubnet1 >> Edge(color="darkblue", style="dashed") >> privateRouteTable
        privateSubnet2 >> Edge(color="darkblue", style="dashed") >> privateRouteTable
        privateRouteTable >> Edge(color="darkblue", style="dashed") >> natGateway
        natPublicIP >> Edge(color="darkblue", style="dashed") >> internetAlt1

        # Public / Internet Gateway
        publicRouteTable = RouteTable("PublicRouteTable")
        publicSubnet1 >> Edge(color="darkgreen", style="bold") << publicRouteTable
        publicSubnet2 >> Edge(color="darkgreen", style="bold") << publicRouteTable
        publicRouteTable >> Edge(color="darkgreen", style="bold") << internetGateway
        internetGateway >> Edge(color="darkgreen", style="bold") << internetAlt1

        diag
