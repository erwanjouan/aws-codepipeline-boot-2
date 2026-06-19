# CloudFormation Modules

- Blog
    - [Introducing AWS CloudFormation modules](https://aws.amazon.com/fr/blogs/mt/introducing-aws-cloudformation-modules/)
- User Guide
    - [Using modules to encapsulate and reuse resource configurations](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/modules.html)

There is an implicit convention for logicalID of resources deployed by the module.

```(LogicalIdOfTheModule)(LogicalIdOfTheResource)```

- Notes
    - Linters are not able to resolve resources from Modules
        - Need to add a line above ```# noinspection YamlUnresolvedReferences```
    - Depends on (a resource outside Cfn Module, whose name is passed as a parameter) is not possible
    - Regional service
        - You have to submit module to every region being used
