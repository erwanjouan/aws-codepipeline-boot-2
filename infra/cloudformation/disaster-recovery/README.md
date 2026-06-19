# Disaster Recovery

[Workshop](https://disaster-recovery.workshop.aws/en/) [Route53 Routing Policy](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/routing-policy.html#routing-policy-multivalue) [Example with API GW](https://devpress.csdn.net/cloudnative/62f2d2757e66823466185b73.html)

## Principle

Perform a multi site (region) deployment to demonstrate various strategies:

| Deployment Strategy Type | Comment                                                                                                                             |
|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Backup and restore       | Storages (EBS Snapshot, RDS Snapshot, EFS Backup) being replicated to recovery site<br/>Restore from backup (cf. AWS Backup service |
| Pilot light              | AMIs are present in recovery region<br/>No traffic can be handled at the beginning of disaster recovery                             |
| Warm Standby             | Reduced capacity in recovery region<br/>Traffic can be handled                                                                      |
| Multi-site active/active | Full capacity in primary and fallback region<br/>Routing policy: GeoLocation Routing with healthcheck                               |

Services that helps DR :

- Aurora Global Database (cross region replication)

## Pre-requisites

According to [this video](https://www.youtube.com/watch?v=KVDt4559cTs) for multi-account deployment, we need to
create [doc](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/stacksets-prereqs-self-managed.html) :

-
AdministrationRole [template](https://s3.amazonaws.com/cloudformation-stackset-sample-templates-us-east-1/AWSCloudFormationStackSetAdministrationRole.yml)
in the administrator account
-
ExecutionRole [template](https://s3.amazonaws.com/cloudformation-stackset-sample-templates-us-east-1/AWSCloudFormationStackSetExecutionRole.yml)
in each target account, passing Administrator account Id.

[Aws StackSet Workshop](https://catalog.workshops.aws/cfn101/en-US/intermediate/operations/stacksets)

