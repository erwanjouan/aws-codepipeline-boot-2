# asg-lifecycle-hook

- Lifecycle Hook sets the EC2 instance in **Pending:Wait** state while retrieving and starting artifact on instance.
- When setup is done, EC2 completes lifecycle hook to set the instance in **InService** status.

[Medium: Retrieve Instance Metadata Token on AL 2023](https://medium.com/@sumitkumar.it81/get-instance-metadata-in-amazon-linux-2023-al2023-e4bf0611d0ad)

[GitHub: Example of lifecycle policy](https://github.com/aws-samples/amazon-ec2-auto-scaling-group-examples/blob/main/features/lifecycle-hooks/userdata-managed-linux/template.yaml)


