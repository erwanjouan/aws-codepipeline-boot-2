https://aws.amazon.com/blogs/mt/migrate-your-aws-opsworks-stacks-to-aws-systems-manager/


# License
This Beta version script is provided for testing the transition from AWS OpsWorks Stacks to AWS Systems Manager Application Manager. This script is provided to you under the terms of the Apache 2.0 license and is subject to the following restrictions: As a condition of accessing and using this script prior to its public release, you agree that until such public release (i) this script is considered as Amazon’s Confidential Information under the MNDA and you may not disclose any information about this script or redistribute any portion of this script to any third party, and (ii) your rights to use, copy, and prepare Derivative Works of the script are limited to internal use only. If you do not agree with these terms, you may not access or use the script.

These terms must accompany all copies of the script that you distribute internally until the public release.


# Background

We are evaluating an approach in which OpsWorks Stacks customers migrate to a proposed future architecture titled “OpsWorks V2”. Unlike the current OpsWorks Stacks architecture, OpsWorks V2 only uses publicly-available AWS building blocks, including CloudFormation stacks, Auto Scaling groups, EC2 launch templates, Application Load Balancers, the Systems Manager agent and more.

The exporter script helps you provision clones of existing OpsWorks Stacks and Layers using the proposed V2 architecture. The script reads information about an existing OpsWorks layer and can provision a clone (or give you a starter template to modify). The provisioned clone is then registered with Application Manager.

We will also provide a similar EC2-centric oriented customer experience that Stacks has today in the [Instances Tab](./instances_tab/instances_tab.md). Be aware that the experience might vary but we will be happy to receive and address your feedback.

## How the process works

The migration script accepts input, such as the OpsWorks layer to analyze, and does one of the following:

1) Produces a CloudFormation template which, when provisioned, creates a clone of the selected layer that uses our proposed OpsWorks V2 architecture. 

2) Everything in #1, plus provisions the CloudFormation stack, and registers it as an application in Systems Manager Application Manager.


# Known limitations

This is an early build of our export script and the OpsWorks V2 architecture differs greatly from OpsWorks Stacks. The following are some limitations and differences summarized.

* **Limited operating system support** - We do not support running Chef recipes on Windows and CentOS instances. 
* **No support for on-premises instances**
* **No support for EC2 imported instances**
* **No support for built-in Chef 11 layers**
* **No support for installing a user-specified list of operating system packages**
* **EBS Volumes** - the script clones EBS volume information except for actual data.
* **Apps are not supported/migrated** 
* **Chef attributes and data bags are not supported** - Chef attributes (Chef 11.10) and Chef data bags (Chef 12) are also not supported. ** Rather than leveraging data bags in your recipes, we strongly recommend calling AWS APIs to retrieve similar information. Contact us about your use cases and we can help you find alternatives.
* **Limited support for scaled instances** - time based and load based scaling instances are migrated, but, we do not migrate the scaling rules over (that is, they will not scale in the same way). You can make adjustments to the Auto Scaling group to achieve similar things.
* **Permissions** - IAM entities that are defined in the Permissions page for the stack in the OpsWorks console are not created or generated.

 Be sure to give us feedback on these or any other limitations that affect your testing.

# Getting started

## Step 1. Ensure prerequisites are met

The exporter script (stack_exporter.py) is a Python script that you can run locally or on an EC2 instance. Before running the script, make sure the following prerequisites are met:

* **[AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)** must be installed (v1 or v2) and properly configured (for example by running `aws configure`). You can learn more about the AWS CLI here: https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html
* **Python 3.6 or later** must be installed (or come with the AMI).
* **Operating system** - You can download and run the migration script on the following operating systems:
    * Amazon Linux 1 & 2
    * Ubuntu 18.04 LTS, 20.04 LTS, 22.04 LTS
    * Red Hat Enterprise Linux 8
    * Windows Server 2019, Windows 10 Enterprise (Windows Server 2022 is not supported). 

The following are example commands to run for each supported operating system to prepare for using the script. 

**Amazon Linux 2**

```
sudo su
python3 -m pip install pipenv
PATH="$PATH:/usr/local/bin"
yum update
yum install git
```

**Amazon Linux 1**

```
sudo su
PATH="$PATH:/usr/local/bin"
export LC_ALL=en_US.utf-8
export LANG=en_US.utf-8
yum update
yum list | grep python3
yum install python36 // Any python version
yum install git

```


In Python 3.6:
```
python3 -m pip install pipenv==2022.4.8
```
In Python 3.7 and greater:
```
python3 -m pip install pipenv
```


**Ubuntu 18.04, 20.04, 22.04**
```
sudo su
export PATH="${HOME}/.local/bin:$PATH"
apt-get update
apt install python3-pip
apt-get install git // if git is not installed
python3 -m pip install --user pipenv==2022.4.8
```

**Red Hat Enterprise Linux 8**
```
sudo su
sudo dnf install python3 
PATH="$PATH:/usr/local/bin"

yum update
yum install git
python3 -m pip install pipenv==2022.4.8
```

**Windows Server 2019 , Windows 10 Enterprise(*Windows Server 2022 is not supported)***
```
// for Windows Server 2019 install python version >= 3.6.1
pip install pipenv
// install git https://git-scm.com/download/win
```

If you have a Git as a cookbook source, then before running the script on Windows, you should add your Git server to a known_hosts file, so that migration can be done.
In PowerShell create the following function:
```
function add_to_known_hosts($server){
    $new_host=$(ssh-keyscan $server 2> $null)
    $existing_hosts=''
    if (!(test-path "$env:userprofile\.ssh")) {
        md "$env:userprofile\.ssh"
    }
    if ((test-path "$env:userprofile\.ssh\known_hosts")) {
        $existing_hosts=Get-Content "$env:userprofile\.ssh\known_hosts"
    }
    $host_added=0
    foreach ($line in $new_host) {
        if (!($existing_hosts -contains $line)) {
            Add-Content -Path "$env:userprofile\.ssh\known_hosts" -Value $line
            $host_added=1
    }
   }
   if ($host_added) {
       echo "$server has been added to known_hosts."
   } else {
       echo "$server already exists in known_hosts."
   }
}
```
Then, run the function, specifying your Git server, such as `github.com`, `git-codecommit.us-east-1.amazonaws.com` (us-east-1 should be replaced on the region where your CodeCommit repository is located).
```
add_to_known_hosts "git-codecommit.us-east-1.amazonaws.com"
```

## Step 2. Download the script

Download the zip file that contains the migration script and all the relevant files by running the following command:
```
aws s3api get-object \
 --bucket export-opsworks-stacks-bucket-prod-us-east-1 \
 --key export_opsworks_stacks_script.zip export_opsworks_stacks_script.zip
```

Next, install the unzip utility (for Linux):

* `sudo apt-get install unzip`
* `sudo yum install unzip`

Unzip the file:

* Linux
  `unzip export_opsworks_stacks_script.zip`
* Windows using `Expand-Archive` in PowerShell:
  
  `Expand-Archive -LiteralPath <PathToZipFile> -DestinationPath <PathToDestination>`

After the file is unzipped, the following files are available:

* README.md  
* LICENSE  
* NOTICE
* requirements.txt  
* templates/
    * OpsWorksCFNTemplate.yaml
    * MountEBSVolumes.yaml
* opsworks/
* cloudformation/
* instances_tab/
* cfn_stack_deployer.py  
* s3.py  
* stack_exporter_context.py  
* stack_exporter.py  


## Step 3. Run the script

*Note: This script currently is only capable of provisioning single-layer applications in Application Manager. For example, if you run the script twice for two layers in the same stack, the results are two different applications in Application Manager.*

First, set up your environment.

```
pipenv install -r requirements.txt
pipenv shell
```

Then, review the available script parameters.

# Migration Script Parameters

| Flag                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Required | Default                                                           | 
|--------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|-------------------------------------------------------------------|
| `--layer-id`	                  | The OpsWorks ID of the layer to export. A CloudFormation template will be exported for the specified layer.ID.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | Yes      |                                                                   |
| `--region`                     | Region of OpsWorks stack. If your OpsWorks stack region and API endpoint region are different use the stack region. This is the same region as the other resources part of your OpsWorks stack (for example, EC2 instances and subnets).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | No       | us-east-1                                                         |
| `--provision-application`      | By default this script provisions the application exported by the CloudFormation template. Add this flag and set it to False to skip the provisioning of the CloudFormation stack.                                                                                                                                                                                                                                                                                                                                                                                                                                                           | No       | TRUE                                                              |
| `--launch-template`            | This flag defines whether to use an existing launch template, or create a new launch template. You can create a new launch template that uses recommended instance properties, or instance properties that match an online instance.<br/>The possible input values are:<br/>1. RECOMMENDED - recommended instance properties are copied from the latest AMI for the OpsWorks stack's operating system and c5.large instance size. <br/>2. MATCH_LAST_INSTANCE - the latest available online instance properties are used.<br/>3. <launch template id>/<version> - an existing launch template is used, where the <version> value is optional. If you do not provide a value for <version>, a default version is used. | No | RECOMMENDED                                                       |
| `--system-updates`             | The flag defining whether to perform kernel and packages update on boot or not.<br/>The possible input values are:<br/>1. `ALL_UPDATES` - performs system updates (kernel and packages) on instance boot.<br/>2. `NO_UPDATES` - does not perform system updates.<br/>3. `MATCH_LAYER_SETTINGS` - uses the OpsWorks layer's or instance's `InstallUpdatesOnBoot` property to define whether to install system updates or not.                                                                                                                                                                                                                    | No       | ALL_UPDATES                                                       |
| `--http-username`              | The name of the Systems Manager SecureString parameter which holds the username used to authenticate to the HTTP Archive where the custom cookbooks are stored.	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | No       |                                                                   |
| `--http-password`              | The name of the Systems Manager SecureString parameter which holds the password used to authenticate to the HTTP Archive where the custom cookbooks are stored.	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | No       |                                                                   |
| `--repo-private-key`           | The name of the Systems Manager SecureString parameter which holds the SSH key used to authenticate to the repository where the custom cookbooks are stored. If the repository is on GitHub, generate a new Ed25519 SSH key, otherwise the connection to the GitHub repository will fail.                                                                                                                                                                                                                                                                                                                                                     | No       |                                                                   |
| `--lb-type`                    | The type of load balancer (if any) to create when migrating your existing Classic Load Balancer.<br />The possible input values are:<br />1. `ALB` - Application Load Balancer.<br />2. `Classic` - Classic Elastic Load Balancer.<br />3. `None` - skip creation.                                                                                                                                                                                                                                                                                                                                                                                                  | No       | ALB                                                               |
| `--lb-access-logs-path`        | The path to an existing S3 bucket and prefix to store the load balancer access logs. S3 bucket must be in the same region as the load balancer. If path is not provided and `--lb-type` is not set to `None`, a new S3 bucket and prefix will be created. An appropriate bucket policy with this prefix must also be in place. The input should follow the format `<bucket name>/<prefix>`.                                                                                                                                                                                                                                                      | No       |                                                                   |
| `--enable-instance-protection` | Creates a custom termination policy (Lambda function) for your Auto Scaling group. EC2 instances with this specific tag are protected from scale in events. The tag key that must be applied to instances to be protected from scale in events is `protected_instance`.                                                                                                                                                                                                                                                                                                                                                                                        | No       | FALSE                                                             |
| `--command-logs-bucket`        | The name of an existing S3 bucket to store the AWS-ApplyChefRecipe and MountEBSVolumes logs. In the absence of this flag, a bucket will be created.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | No       | `aws-opsworks-application-manager-logs-<account id>`              |
| `--custom-json-bucket`         | The name of an existing S3 bucket to store the Custom JSON. In the absence of this flag, a bucket will be created.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | No       | `aws-apply-chef-application-manager-transition-data-<account id>` |


**Notes:** 

* If you use a private GitHub repository a new Ed25519 host key for SSH is required because GitHub changed which keys are supported in SSH and removed unencrypted Git protocol. For more information refer [here](https://github.blog/2021-09-01-improving-git-protocol-security-github/). After a new Ed25519 key is generated, create a Systems Manager SecureString parameter for this SSH key and use the `--repo-private-key` parameter. In the FAQ section, you will find more information on how to create a Systems Manager SecureString parameter.
* `--http-username` , `--http-password` and `--repo-private-key` all refer to the name of a Systems Manager SecureString parameter. The automation documents use the parameter names to run the AWS-ApplyChefRecipes document.
* `--http-username` requires that you set --http-password.
* `--http-password` and `--repo-private-key` should not be set together. Provide Systems Manager SecureString parameter names of either an SSH key (--repo-private-key) or a repository username and password (`--http-username` & `--http-password`)
* `--http-password` requires that you set  `--http-username`.

Show available options by running `python3 stack_exporter.py --help`

At this point, you’re ready to get started! We’ve provided some example commands.

### Example command 1: Provision an Application Manager application

The following command reads information about an OpsWorks layer you already have, and provisions the OpsWorks V2 architecture needed to achieve similar needs using the Chef version configured in the stack. All required resources (for example, Auto Scaling groups, etc.) are provisioned through CloudFormation and registered as an application in Systems Manager Application Manager.

```
python3 stack_exporter.py \
  --layer-id SOME_OPSWORKS_LAYER_ID \
  --region SOME_STACK_REGION
```

### Example command 2: Generate a template only

The following command reads information about an OpsWorks layer you already have, and generates a template, that if provisioned, can achieve similar needs using Chef 14. No resources have been provisioned. You must review the template in the Application Manager Template Library in Systems Manager, where you can also provision the template if you want.

```
python3 stack_exporter.py \
  --layer-id SOME_OPSWORKS_LAYER_ID \
  --region SOME_STACK_REGION \
  --provision-application FALSE
```

## Step 4. Post provisioning

After you run the script, there are two possible outputs, depending on what was passed to the `-—provision-application` parameter: 1) a CloudFormation template, or 2) a CloudFormation stack.

### Step 1 - Provision a CloudFormation stack
*Note: This step is needed only if you passed the `--provision-application FALSE` parameter to the script.*

The script execution output includes the name and URL of the CloudFormation template. The new template represents the proposed OpsWorks V2 architecture, which can replace the stack you targeted. The script does not provision the template for you if you added the `--provisionApplication False` parameter. You must provision it yourself by [using CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/stacks.html) or (recommended) [the template library in Application Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/application-manager-working-templates-overview.html).

### Step 2 - Review resources
At this point, you should review the resources provisioned on your behalf before taking any instances online.

* Open the CloudFormation console and review the Resources tab of the provisioned stack. The list of resources includes an EC2 Auto Scaling group, which we recommend you explore in the Auto Scaling console or CLI. 
* Open [Application Manager in the Systems Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/application-manager.html) console, where you can see all resources in your migrated OpsWorks stack and layers. Each application will be named after the OpsWorks stack name (`app-<stack_name>-suffix, where suffix is first 6 letters of stack_id`)

### Step 3 - Start a test instance
After provisioning the OpsWorks V2 architecture template, it is ready for testing. For your safety, no instances are running in it yet. The Auto Scaling group that manages your V2 architecture has “minimum instances”, “maximum instances”, and “desired instances” all set to 0. To take your first instances online, set the number of maximum instances and desired instances to a number that makes sense for your application. We strongly recommend setting all values to 1, which signals the Auto Scaling group to bring a single instance online. Then, verify that the instance performed all actions as expected, including running your custom Chef recipes. 

### Step 4 - Review the test instance
After you’ve started a test instance, verify that it runs as expected. To do that, review the Chef logs (for `startup` and `terminate`), check Application Load Balancer connectivity and health, and ensure the instance passes all Auto Scaling and Application Load Balancer health checks (if you’ve configured any). Optionally, connect to the instance by using SSH to do some testing. See the FAQ question, “Where are Chef recipe logs stored” for more information about where Chef execution logs are stored for you to perform any validations you need. After you have verified Chef recipes run as expected, we also recommend decreasing the Auto Scaling group capacity. This terminates the instance that Auto Scaling launched in Step 3. If you have any custom termination recipes, verify that they worked as expected. 

### Step 5 - Go big!
At this point, you’ve verified that a single instance is working as expected under the OpsWorks V2 architecture. If you want, you can expand on your test by increasing the min, max and desired instance parameters on the Auto Scaling group to a larger number, to perform a larger-scale test.

## Cleanup

The migration script creates multiple resources by using CloudFormation and also directly by using the AWS APIs. 
The creation of certain resources depends on the options you have provided when  executing the script. The output 
of the script will specify the resources that are created and need to be deleted if necessary. Run the below commands 
using the AWS CLI to delete the resource that the migration script created to clone the layer.

1. Describe your OpsWorks Stacks and find the stack ID of the stack from which you migrated a layer
   (Take note of the first 6 digits of your stack ID):
   ```
   aws opsworks describe-stacks --region <AWS-Region>
   ```
2. Delete the CloudFormation stack that the migration script created to clone your layer:
   ```
   aws cloudformation delete-stack --stack-name <OpsWorks stack name>-<OpsWorks layer name>-<first 6 characters of the the OpsWorks stack ID> --region <AWS-Region>
   
   aws cloudformation wait stack-delete-complete --stack-name <OpsWorks stack name>-<OpsWorks layer name>-<first 6 characters of the the OpsWorks stack ID> --region <AWS-Region>
   ```
3. Delete the MountEBSVolumesStack created by the migration script:
   ```
   aws cloudformation delete-stack --stack-name MountEBSVolumesStack
   
   aws cloudformation wait stack-delete-complete --stack-name MountEBSVolumesStack --region <AWS-Region>
   ```
4. Delete the Launch Template created by the migration script (Optional, you may have provided your
   own as a parameter to the migration script):
   ```
   aws ec2 delete-launch-template --launch-template-name <OpsWorks stack name>-<OpsWorks layer name>-LaunchTemplate-<first 6 characters of the the OpsWorks stack ID>
   ```
5. Delete the IAM instance profile and IAM role created by the migration script (Optional, you may have
   provided your own Launch Template as a parameter to the migration script):
   ```   
   aws iam remove-role-from-instance-profile --instance-profile-name <OpsWorks stack name>-<OpsWorks layer name>-AsgInstanceProfile-<first 6 characters of the the OpsWorks stack ID> --role-name <OpsWorks stack name>-<OpsWorks stack name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID>
   
   aws iam delete-instance-profile --instance-profile-name <OpsWorks stack name>-<OpsWorks layer name>-AsgInstanceProfile-<first 6 characters of the the OpsWorks stack ID>
   
   aws iam detach-role-policy --role-name <OpsWorks stack name>-<OpsWorks layer name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID> --policy-arn arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore
   
   aws iam detach-role-policy --role-name <OpsWorks stack name>-<OpsWorks layer name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID> --policy-arn arn:aws:iam::aws:policy/AmazonSSMPatchAssociation
   
   aws iam detach-role-policy --role-name <OpsWorks stack name>-<OpsWorks layer name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID> --policy-arn arn:aws:iam::aws:policy/AmazonElasticFileSystemsUtils
   
   aws iam delete-role-policy --role-name <OpsWorks stack name>-<OpsWorks layer name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID> --policy-name SSM-Automation-Permission-to-s3-Policy

   aws iam delete-role --role-name <OpsWorks stack name>-<OpsWorks layer name>-InstanceProfileRole-<first 6 characters of the the OpsWorks stack ID>
   ```
6. Delete the Resource Group created by the migration script:
   ```
   aws resource-groups delete-group --group-name app-<OpsWorks stack name>-<first 6 characters of the the OpsWorks stack ID> --region us-west-2
   ```
7. Delete the S3 Buckets created by the migration script (Optional, you may have provided your own as a
   parameter to the migration script):
   ```
   aws s3 rm --recursive s3://aws-opsworks-stacks-access-logs-<account id>
   
   aws s3 rb s3://aws-opsworks-stacks-access-logs-<account id>
   
   aws s3api delete-objects --bucket aws-opsworks-stacks-transition-logs-<Account id> --delete "$(aws s3api list-object-versions --bucket "aws-opsworks-stacks-transition-logs-<account id>" --output=json --query='{Objects: Versions[].{Key:Key,VersionId:VersionId}}')"
   
   aws s3api delete-objects --bucket aws-opsworks-stacks-transition-logs-<Account id> --delete "$(aws s3api list-object-versions --bucket "aws-opsworks-stacks-transition-logs-<account id>"--output=json--query='{Objects: DeleteMarkers[].{Key:Key,VersionId:VersionId}}')"
   
   aws s3 rm --recursive s3://aws-opsworks-stacks-transition-logs-<account id>
   
   aws s3 rb s3://aws-opsworks-stacks-transition-logs-<account id>
   
   aws s3 rm --recursive s3://aws-opsworks-application-manager-logs-<Account id>
   
   aws s3 rb s3://aws-opsworks-application-manager-logs-<account id>
   
   aws s3 rm --recursive s3://aws-apply-chef-application-manager-transition-data-<Account id>
   
   aws s3 rb s3://aws-apply-chef-application-manager-transition-data-<Account id>
   ```
   
## FAQ

**“What OpsWorks Stack versions can the script migrate?”**

Currently, customers can only export Chef 11.10 and Chef 12 Amazon Linux, Amazon Linux 2, Ubuntu, Red Hat Enterprise Linux 7  stacks. Windows stack migration is not yet supported. 

**“What Chef versions can OpsWorks V2 instances use?”**

Currently, they can use Chef versions 11 through 14.

**“What OpsWorks Stack repository types can the script migrate?”**

Currently, customers can migrate S3, Git and HTTP repository types. 

**“How can I continue using private Git as a source?”**

If you have a GitHub repository, generate a new Ed25519 SSH key, and link it with your GitHub account. Then, create a Systems Manager Secure String parameter, and provide it to the script. For any other Git repository, create a Secure String parameter for the used SSH key, and provide it to the script.

**“What SSH keys can I use to access my OpsWorks V2 instances?”**

During migration the SSH keys and instances configured in the stack are migrated. You should use the SSH keys to access your instance. If SSH keys are provided to both stack and instance, the keys from the stack are used. If you are not sure, view one of the instances in the EC2 console, and the details page in the EC2 console shows the SSH key used.

**“My instances are automatically scaling in and out. Why?”**

Auto Scaling scales instances (in/out) based on what scaling rules exist on the Auto Scaling group. You set a minimum number of instances allowed, a maximum number of instances allowed, a desired number of instances, and some scaling rules (or a default scaling policy). The Auto Scaling group automatically scales your capacity based on the parameters you provided.

**“How can I turn off scaling?”**

To disable scaling *entirely* in Auto Scaling, set all three capacity metrics (min, max and desired number of instances) on the Auto Scaling group to the same number. For example, if you want 10 instances always on, set all three values to 10.

**“How can I perform kernel and packages update on EC2 instance after it was launched?”**

By default, kernel and packages update happens during EC2 boot. If you need at some point to perform kernel and packages update later on already launched EC2 instance (for example, after running deploy or configure recipes), you need to follow the steps:

1. Connect to your EC2 instance.
2. Create the `perform_upgrade` function and run it.
```
perform_upgrade() {
    #!/bin/bash
    if [ -e '/etc/system-release' ] || [ -e '/etc/redhat-release' ]; then
     sudo yum -y update
    elif [ -e '/etc/debian_version' ]; then
     sudo apt-get update
     sudo apt-get dist-upgrade -y
    fi
}
perform_upgrade
```
3. Very often after kernel and packages update, the EC2 instance reboot is required. To understand whether your instance needs a reboot or not, add a `reboot_if_required` block, and run it.

```
reboot_if_required () {
 #!/bin/bash
 if [ -e '/etc/debian_version' ]; then
   if [ -f /var/run/reboot-required ]; then
     echo "reboot is required"
   else
     echo "reboot is not required"
   fi
 elif [ -e '/etc/system-release' ] || [ -e '/etc/redhat-release' ]; then
  export LC_CTYPE=en_US.UTF-8
  export LC_ALL=en_US.UTF-8
  LATEST_INSTALLED_KERNEL=`rpm -q --last kernel | perl -X -pe 's/^kernel-(\S+).*/$1/' | head -1`
  CURRENTLY_USED_KERNEL=`uname -r`
  if [ "${LATEST_INSTALLED_KERNEL}" != "${CURRENTLY_USED_KERNEL}" ];then
     echo "reboot is required"
  else
     echo "reboot is not required"
  fi
 fi
}
reboot_if_required
```
4. If after running the `reboot_if_required` function shows a `reboot is required` message, reboot the EC2 instance. If a `reboot is not required` message is returned, then reboot is not required.

**“Why don’t I see any of the data that was on the EBS volumes for my OpsWorks instances”**

This script creates a replacement architecture for your OpsWorks stacks and layers. It does not actually migrate instances or any data on them. The script migrates the configuration of EBS volumes in the layer level, attaches and then mounts the empty EBS volumes to launched EC2 instances. To pull data from your old instances into the new setup:

1. [Take a snapshot of the EBS volumes of your old OpsWorks instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-creating-snapshot.html).
2. [Create a volume from the snapshot](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-creating-volume.html#ebs-create-volume-from-snapshot).
3. After you have updated the Auto Scaling group capacity, [attach the volume to the instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-attaching-volume.html).

That said, this counters Auto Scaling best practices. Ideally, every instance in an Auto Scaling group should be easily replaced (for example, to handle failure and scaling cases).

**“Why are EBS volumes that are described in my launch template not mounted?”**

An OpsWorks layer’s EBS volumes are migrated, attached and mounted only when you don’t provide a launch template, and a launch template is created by the migration script. In this case, when an Auto Scaling group launches a new EC2 instance, it automatically attaches the EBS volumes. Then, the `SetupAutomation` command mounts the attached volumes to the mount points taken from layer settings.

If you provide a launch template with EBS volumes, the volumes are attached but not mounted. You can mount attached EBS volumes afterwards by running the `MountEBSVolumes` RunCommand document that the migration script created on the launched EC2 instance.

**“Where are the logs stored for Chef recipes and mounting EBS volumes”**

Chef and EBS volumes mount activity details are logged to a bucket created by the script with the following naming pattern: `aws-opsworks-stacks-application-manager-logs-<account id>`. The Chef recipe run logs are stored in the `ApplyChefRecipes` prefix, and EBS volumes mount activity logs are stored in the `MountEBSVolumes` prefix. All layers that are migrated from the same stack send logs to the same bucket.

Notes: 

* A lifecycle rule is applied to this bucket, so logs are deleted after 30 days. You should change this rule if you want logs retained longer than 30 days. Be aware that re-runs of the export script may change your log retention settings. A prefix is configured for this rule, and logs are uploaded to the buckets with this prefix.
* Only `setup` and `terminate` Chef Recipes are logged to the bucket for now.

**“Does the migration script have a debug log?”**

Yes, the script places debug logs to a bucket named `aws-opsworks-stacks-transition-logs-<account id>` that migration creates for you. You can find logs in the “migration_script” folder of the bucket in subfolders named to match the name of the layer that you migrated.

**“Does the migration script support CloudFormation template versioning?”** 

Yes, the script generates Systems Manager documents of type CloudFormation that create a replacement for the layer or stack you want to migrate. Running the script again (even with the same parameters) exports a new version of a previously-exported template of a layer. The template versions are also stored in a S3 bucket where the script logs are placed.

**“Can I migrate multiple layers?”** 

The flag `--layer-id` passes in a single layer. To migrate multiple OpsWorks layers, rerun the script passing in a different `--layer-id`. 
If the layers are part of the same OpsWorks stack they will appear together within the same application in the application manager. 

1. Go to `Systems Manager`, `Application Manager` in the AWS console. Your newly created applications will be in `Custom applications`. 
2. Select the application corresponding to your migrated OpsWorks stack. This is named `app-<OpsWorks stack name>-<first 6 characters of the the OpsWorks stack ID>`.
3. The top-level element, starting with `app`, corresponds to your OpsWorks stack. This includes components which correspond to your OpsWorks layers.
4. To see all resources from all components, view the top-level element (prefix app). To see only resources that are related to a specific layer, choose the corresponding component.
5. The components representing OpsWorks layers are also visible from the Custom applications section as individual applications. 

**“Can we protect instances in the newly created Auto Scaling group from different termination events?”**

Yes, you can protect instances using the flag `--enable-instance-protection` that

1. Adds a custom termination policy to your new Auto Scaling group.
2. Suspends the `ReplaceUnhealthy` process.

By manually adding a tag to an instance that is part of that Auto Scaling group, where the tag key must be `protected_instance`, that instance is protected from the following termination events: 

* Scale in events
* Instance refresh
* Rebalancing
* Instance max lifetime 
* Allow listing instances termination
* Termination and replacement of unhealthy instances 

Notes:
* Tag key must be `protected_instance` and casing for it is important. Any instance with that tag key regardless of tag value is protected.
* To reduce how long the custom termination policy runs, you can increase the default number of instances the Lambda function uses to filter for protected instances. You can update the Lambda function code variable `default_sample_size`. The default value is 15. If you do this, you might also need to increase the memory allocated to the Lambda function. This increases the costs of your Lambda function.

**“I have enabled Auto Scaling group protected instances and am now trying to delete my CloudFormation stack. I am unable to delete the stack and it gets stuck on deleting the Auto Scaling group.”**

If you have added the `--enable-instance-protection` flag, and some of your Auto Scaling group instances are protected, your CloudFormation stack cannot be completely deleted. To delete the entire stack, you must remove the protective `protected_instance` tag from the protected EC2 instances. This lets the Auto Scaling group scale down completely and be deleted, which lets you delete the CloudFormation stack successfully.

**“How do I create an Systems Manager SecureString parameter?”**

A Systems Manager SecureString parameter is required if you are passing any of the following flags: `--http-username` , `--http-password` and `--repo-private-key`.

* To create a Systems Manager SecureString in the AWS console, open Systems Manager, then Parameter store. Choose ‘Create parameter’, and provide your parameter with a name. The name is passed with the CLI argument. Choose SecureString as the type, and leave the data type as text. Enter the secret value. As a Systems Manager SecureString parameter is encrypted, configure the parameter to use the correct KMS keys. Then choose ‘Create parameter’.

* To create a Systems Manager SecureString parameter by using the AWS CLI, run the following command: `aws ssm put-parameter --name "parameter-name" --value "parameter-value" --type "SecureString"`

This will use the default AWS managed key. See this link [here](https://docs.aws.amazon.com/systems-manager/latest/userguide/param-create-cli.html#param-create-cli-securestring) for examples on using different keys. 

**“What load balancers are available to me when I'm using the migration script?”**

When you use the migration script, you have three load balancer options:
1. Recommended option: To create a new Application Load Balancer. This is the default option (you can also use the flag `--lb-type ALB`). This is a newer generation of load balancer.
2. If an Application Load Balancer is not an option, the next recommendation is to use the flag `--lb-type CLASSIC` to create a Classic Load Balancer. This ensures that your existing Classic Load Balancer attached to your OpsWorks layer is entirely separate from your new application. A Classic Load Balancer is a previous generation load balancer.
3. **The final option is experimental and is not supported.** You can attach your OpsWorks layer load balancer to your Auto Scaling group. However, upon every configure event, instances outside of your OpsWorks layer (for example, your Auto Scaling group instances) will be de-registered from your load balancer. See [this document](https://docs.aws.amazon.com/opsworks/latest/userguide/layers-elb.html) for more information. If this is your selected option, this should only be for a short period of time prior to turning off your OpsWorks layer.

To attach your existing OpsWorks layer load balancer to your Auto Scaling group:

1. Run the migration script with the flag `--lb-type None`. This will not clone or create a load balancer.
2. Once the CloudFormation stack is deployed, update your Auto Scaling group min max and desired values, and test out your application.  
3. From the output where the migration script was run go to the template link (the link following the text ‘Link to the template’).*
4. Under `Actions` choose `Edit`.
5. Update the property `LoadBalancerNames` within the ApplicationAsg resource in the CloudFormation template (see example at the end of these steps).
6. If you require your Auto Scaling group instances health check to also use the health check run by your Load Balancer, then remove the `HealthCheckType` value and enter ELB (see example). If you only require EC2 health checks, leave the template as is.
7. Save your changes. This will create a new default version of the template. If this is the first run of this layer, and the first set of changes you have edited in the console, the new version will be 2. 
8. Under `Actions` choose `Provision stack`. Then confirm to use the default version of the template. Ensure `Select an existing stack` is selected and choose the CloudFormation stack to update.
9.  Choose `Next` through until the Review and Provision page. Check the boxes that say **"I acknowledge that AWS CloudFormation might create IAM resources with custom names”** and **"I understand that changes in the selected template can cause AWS CloudFormation to update or remove existing AWS resources"** then choose `Provision stack`.

*If you have closed the terminal you can access the template by going to `Systems Manager`, `Application Manager`, `CloudFormation stacks` and `Template library` in the AWS console. Locate your template in the `Owned by me` section. The template name can be searched for and will start with <OpsWorks stack name>-<OpsWorks layer name>.

The following block shows the two properties that were added and updated within the ApplicationAsg resource. 

```
  ApplicationAsg:
    DependsOn: CustomTerminationLambdaPermission
    Properties:
    #(other properties in ApplicationAsg to remain unchanged)
      LoadBalancerNames:
        - <name of your load balancer>
      HealthCheckType: ELB
```
*Rollback steps:*
If you see any issues and want to roll back, under `Actions`, choose `Provision stack`. Choose `Pick one of the existing versions`, and then choose the previous template version. Ensure `Select an existing stack is selected`, and choose the CloudFormation stack to update.

**“My OpsWorks layer has custom cookbook configure recipes. Are these migrated? How do I run them?”**

Configure custom cookbooks are not supported to run during a setup event (Auto Scaling group scale out event). If you have custom cookbook configure recipes, these will be migrated and a Systems Manager Automation runbook will be created for you. However, the recipes must be run manually.

To run your configure recipes, open `Application Manager` in the AWS console and identify the custom application you are working with (this starts with `app-<OpsWorks stack name>`). In the Resources tab, choose the configure runbook, then choose `Execute Automation`. Choose the instance IDs where you want to run the configure recipes, and then choose `Execute`.

**“Can I rerun my deploy and undeploy recipes on my newly created instances?”**

Depending on your layer configuration, we can create three possible Automation runbooks:

* Setup 
* Configure
* Terminate

We can also create the following Systems Manager parameters that contain input values for the AWS-ApplyChefRecipes Run Command document:

* Setup
* Deploy
* Configure
* Undeploy
* Terminate

When a scale out event (increase in number of Auto Scaling group instances) occurs, the setup Automation runbook  automatically runs. This includes the setup and deploy custom cookbook recipes from your original OpsWorks layer.  When a scale in event (decrease in number of Auto Scaling group instances) occurs, the terminate Automation runbook automatically runs. These are the shutdown recipes from your original OpsWorks layer.

To run the deploy, undeploy or configure recipes manually, on an as-needed basis:

1. Go to `Systems Manager`, `Application Manager` in the AWS console. Choose `Custom applications` to view your newly created applications.
2. Select the application corresponding to your migrated OpsWorks stack. This is named `app-<OpsWorks stack name>-<first 6 characters of the the OpsWorks stack ID>`. 
3. In the `Resources` tab, choose the configure Automation runbook, then choose `Execute Automation`.
5. In `applyChefRecipesPropertiesParameter` Automation runbook input parameter, reference the correct Systems Manager parameter. The Systems Manager parameter name follows the format `/ApplyChefRecipes-Preset/<OpsWorks stack name>-<OpsWorks layer name>-<first 6 characters of the the OpsWorks stack ID>/<Event>`, where `<Event>` can be `Configure`, `Deploy` or `Undeploy` depending on the recipes you want to run.
4. Choose the instance IDs where you want to run the recipes and choose `Execute`. 

**"Can I change what subnets my newly created Auto Scaling group spans?"**

By default the Auto Scaling group will span all subnets in your OpsWorks stack VPC. To update which subnets to use, do the following:

1. From the output where the migration script was run go to the template link (the link following the text ‘Link to the template’).*
2. Under `Actions` choose `Provision Stack`. Then confirm to use the default version of the template. Ensure `Select an existing stack` is selected and choose the CloudFormation stack to update. If `--provision-application false` was provided when running the migration script, you will need to create a new CloudFormation stack.
3. Update the parameter `SubnetIds` to be a comma separated list of the subnet IDs for your Auto Scaling group to span.
4.  Choose `Next` through until the Review and Provision page. Check the boxes that say **"I acknowledge that AWS CloudFormation might create IAM resources with custom names”** and **"I understand that changes in the selected template can cause AWS CloudFormation to update or remove existing AWS resources"** then choose `Provision stack`.


**"I have provided an existing S3 bucket and a prefix for my new load balancer access logs. However, when deploying the CloudFormation template I get an access denied error"**

Ensure that your bucket has the correct bucket policy in place for Load Balancer access logs to be published to. See the documentation [here](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/enable-access-logging.html) for help. 

**"I previously used the aws_opsworks_instance data bag reference. How can I determine information about the instances belonging to my newly created Auto Scaling group?"**

OpsWorks Stacks provided an aws_opsworks_instance data bag that enabled customers to enumerate instances belonging to an OpsWorks layer and their properties. Due to the fact that instances in the new architecture are managed by an Auto Scaling group, we have deprecated this data bag. To determine information about your EC2 instances, we recommend doing the following in your recipes:


1. Use the EC2 metadata to determine the instance ID and then the Auto Scaling group that the instance belongs to. 
2. Use the [describe_auto_scaling_groups](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/AutoScaling/Client.html#describe_auto_scaling_groups-instance_method) method to compile an array of instance IDs which belong to the Auto Scaling group.
3. Determine EC2 instance information (such as AMI ID, subnet ID) using the [describe_instances](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/EC2/Client.html#describe_instances-instance_method) method. 

```
chef_gem 'aws-sdk'
require 'aws-sdk'

# Use the following to get information about the EC2 instance on which you are running your recipe.
ec2_metadata = Aws::EC2Metadata.new
region_name = ec2_metadata.get('/latest/meta-data/placement/region') 
credentials = Aws::InstanceProfileCredentials.new()
asg_client = Aws::AutoScaling::Client.new(
    region: region_name,
    credentials: credentials,
  )
ec2_client = Aws::EC2::Client.new(
    region: region_name
  )
ec2_instance_id = ec2_metadata.get('/latest/meta-data/instance-id')

# Step 1: Retrieves the Auto Scaling group name your instance belongs to  
asg_name = asg_client.describe_auto_scaling_instances(
    instance_ids: [
        ec2_instance_id, 
      ], 
)['auto_scaling_instances'][0]['auto_scaling_group_name']
asg_info = asg_client.describe_auto_scaling_groups({
    auto_scaling_group_names: [
        asg_name
    ], 
  })['auto_scaling_groups'][0]

# Step 2: Create a list of instance IDs belonging to your Auto Scaling group   
asg_instances = []
for i in asg_info['instances'] do    
    asg_instances.push(i['instance_id'])
end
# Step 3: 
# Create an array of information on each instance in your Auto Scaling group 
instances_in_asg_information = []
for r in ec2_client.describe_instances({instance_ids: asg_instances})['reservations'] do
    for instance in r['instances'] do
        instances_in_asg_information.push(instance)
    end
end
```

For examples of what your existing recipes can look like, see the appendix at the end of this document. You can compare a related example in the Appendix to see what you might need to change. The examples include one that returns EC2 information on the instance that is running the recipe itself.

**"I previously used the aws_opsworks_command data bag reference. How can I list the commands that have been run on my instance?"**

OpsWorks Stacks provided an aws_opsworks_command data bag that enabled customers to enumerate commands that have been run by an instance belonging to an OpsWorks layer.  Due to the fact that commands in the new architecture are managed by AWS Systems Manager, we have deprecated this data bag.

To determine information about the commands that have been run, we recommend doing the following in your recipes:


1. Using the EC2 metadata determine the instance ID of your instance running the recipe. 
2. Use the [list_command_invocations](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#list_command_invocations-instance_method) method and filter on the instance ID 

```
chef_gem 'aws-sdk'
require 'aws-sdk'

# Step 1: Use the following to get information about the EC2 instance on which you are running your recipe.
ec2_metadata = Aws::EC2Metadata.new
region_name = ec2_metadata.get('/latest/meta-data/placement/region') 
credentials = Aws::InstanceProfileCredentials.new()
ssm_client = Aws::SSM::Client.new(
  region: region_name
)
ec2_instance_id = ec2_metadata.get('/latest/meta-data/instance-id')

# Step 2: 
commands = ssm_client.list_command_invocations({
    instance_id: ec2_instance_id,
 })

```

The commands are run by triggering an Automation runbook. See the appendix for a full example of how to get a list of Automation runbooks using the [describe_automation_executions](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#describe_automation_executions-instance_method) method. This will return a filtered response based on different key values.


# Appendix

### aws_opsworks_instance code examples
If you are using the instance data bag, you might have something similar to the following code in your own Chef recipes.
```
### Customer's recipes ###
# Example 1 - See solution 1
instance = search("aws_opsworks_instance", "self:true").first
file node['setupfilepath'] do
  content "Instance ID '#{instance['instance_id']}'\n has an ami of: '#{instance['instance_id']}'"
  action :create
end

# Example 2 - See solution 2 
# Example 2 will loop through the whole data bag, and for each data bag item, will log the EC2 instance ID.
data_bag("aws_opsworks_instance").each do |data_bag_item|
  instance = data_bag_item("aws_opsworks_instance", data_bag_item)
  Chef::Log.info("********** The EC2 instance ID is '#{instance['ec2_instance_id']}' **********")
end

# Example 3 uses the hostname (which is the name of the JSON file) to obtain a data_bag_item
# This is no longer applicable as the instances are not searched by using host names
# Instead you will need to search by EC2 Instance ID
# A list of instance IDs which are part of the same Auto Scaling group your instance (the instance you are running your recipes on) belongs to can be determined, see solution 2
instance_1 = data_bag_item("aws_opsworks_instance", "hostname")
Chef::Log.info("********** The instances's AZ is '#{instance_1['availability_zone']}' **********")
```

To update your recipes, and retrieve information about the **instance your recipe is running on**, see the the following commands.
```
# Solution 1
chef_gem 'aws-sdk'
chef_gem 'json'

require 'aws-sdk'
require 'json'
# The preceding imports the required Ruby gems for the Chef recipe to run.
# Use the following to get information about the EC2 instance on which you are running your Chef recipe.
ec2_metadata = Aws::EC2Metadata.new
credentials = Aws::InstanceProfileCredentials.new()
region_name = ec2_metadata.get('/latest/meta-data/placement/region') 
ec2_client = Aws::EC2::Client.new(
  region: region_name
)
ec2_instance_id = ec2_metadata.get('/latest/meta-data/instance-id') 
ec2_info=ec2_client.describe_instances({
    instance_ids: [
        ec2_instance_id, 
    ], 
  })['reservations'][0]['instances'][0]
  
# Once ec2_info has been assigned 
# Use the following to access different instance attributes.
ami_id = ec2_info['image_id']
availability_zone = ec2_info['placement']['availability_zone'] 
public_dns = ec2_info['public_dns_name']
public_ip = ec2_info['public_ip_address']
subnet_id = ec2_info['subnet_id']
instance_type = ec2_info['instance_type']
private_dns = ec2_info['private_dns_name']
private_ip = ec2_info['private_ip_address']
architecture = ec2_info['architecture']
created_at = ec2_info['launch_time']
virtualization_type = ec2_info['virtualization_type']
ebs_optimized = ec2_info['ebs_optimized']
root_device_type = ec2_info['root_device_type']
for i in ec2_info['block_device_mappings'] do    
    if i['device_name'] == ec2_info['root_device_name']
        root_device_volume_id = i['ebs']['volume_id']
        break
    end
end
os = ec2_info['platform_details']
aws_opsworks_instance={
    :ami_id => ami_id,
    :availability_zone => availability_zone,
    :ec2_instance_id => ec2_instance_id,
    :os => os,
    :public_dns => public_dns,
    :root_device_type => root_device_type,
    :root_device_volume_id => root_device_volume_id
    :subnet_id => subnet_id,
    :architecture => architecture,
    :launched_at => created_at,
    :instance_type => instance_type,
    :private_ip => private_ip,
    :private_dns => private_dns,
    :public_ip => public_ip,
    :virtualization_type => virtualization_type,
    :ebs_optimized => ebs_optimized
}
# The following will create a json file with the preceding dictionary.
file "/tmp/ec2_opsworks_instance.json" do
 content "'#{aws_opsworks_instance.to_json}'"
 action :create
end

```
To determine information on all instances in your Auto Scaling group, use the following.
```
# Solution 2
chef_gem 'aws-sdk'
chef_gem 'json'
require 'aws-sdk'
require 'json'
# The preceding imports the required Ruby gems for the Chef recipe to run.
# Use the following to get information about the EC2 instance on which you are running your recipe.
ec2_metadata = Aws::EC2Metadata.new
region_name = ec2_metadata.get('/latest/meta-data/placement/region') 
credentials = Aws::InstanceProfileCredentials.new()
asg_client = Aws::AutoScaling::Client.new(
    region: region_name,
    credentials: credentials,
  )
ec2_client = Aws::EC2::Client.new(
    region: region_name
  )
ec2_instance_id = ec2_metadata.get('/latest/meta-data/instance-id')

# Retrieves the Auto Scaling group name your instance belongs to  
asg_name = asg_client.describe_auto_scaling_instances(
    instance_ids: [
        ec2_instance_id, 
      ], 
)['auto_scaling_instances'][0]['auto_scaling_group_name']
asg_info = asg_client.describe_auto_scaling_groups({
    auto_scaling_group_names: [
        asg_name
    ], 
  })['auto_scaling_groups'][0]

# Create a list of instance IDs belonging to your Auto Scaling group   
asg_instances = []
for i in asg_info['instances'] do    
    asg_instances.push(i['instance_id'])
end
#You can access an individual instance information by doing the following
#asg_instances[0] - where 0 indicates the instance index you are retrieving the information for
ec2_info=ec2_client.describe_instances({
    instance_ids: [
        asg_instances[0], 
    ], 
  })['reservations'][0]['instances'][0]

# Create an array of information on each instance in your Auto Scaling group 
instances_in_asg_information = []
for r in ec2_client.describe_instances({instance_ids: asg_instances})['reservations'] do
    for instance in r['instances'] do
        instances_in_asg_information.push(instance)
    end
end

#Loop through the array and use information, like in solution 1        
for i in instances_in_asg_information do
    file "/tmp/'#{i['instance_id']}'.json" do
        content "'#{i.to_json}'"
        action :create 
    end
end
```

### aws_opsworks_instance code examples

The following examples are centered around Automation runbooks. These runbooks trigger other commands that are run on EC2 instances. An Automation runbook may trigger multiple commands.

The [describe_automation_executions](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#describe_automation_executions-instance_method) method will return a filtered response based on different key values. As not all of these can be used as a filter for the [describe_automation_executions](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#describe_automation_executions-instance_method) method you must first collect a list of executions. 

The following list explains the ‘mapping’ from aws_opsworks_command data bag to Automation runbook execution attributes:

The [describe_automation_executions](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#describe_automation_executions-instance_method) method will return a filtered response based on different key values. As not all of these can be used as a filter for the [describe_automation_executions](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SSM/Client.html#describe_automation_executions-instance_method) method you must first collect a list of executions. 

For example, you cannot filter this method using Instance IDs. Therefore, you must construct an array of all the automation executions using this method. Then iterate through this list using the [get_automation_execution](https://docs.aws.amazon.com/sdk-for-ruby/v2/api/Aws/SSM/Client.html#get_automation_execution-instance_method) method to determine if the parameter of the execution matches the instance ID. A full example of this can be seen in the appendix. 

If you are using the command data bag, you might have something similar to the following code in your own Chef recipes.

```
# Customers recipe 
command = search("aws_opsworks_command").first
Chef::Log.info("********** The command's type is '#{command['type']}' **********")
Chef::Log.info("********** The command was sent at '#{command['sent_at']}' **********")

search("aws_opsworks_command").each do |command|
  Chef::Log.info("********** The command's type is '#{command['type']}' **********")
  Chef::Log.info("********** The command was sent at '#{command['sent_at']}' **********")
end
```

To update your recipes and get information about the different commands that have been run, see the following script.
```
chef_gem 'aws-sdk'
chef_gem 'date'

require 'aws-sdk'
require 'date'

# The following script sets up the Systems Manager client, using the region from the instance where the recipe is running.
# The credentials are set using the instance profile that is attached. 
ec2_metadata = Aws::EC2Metadata.new
region_name = ec2_metadata.get('/latest/meta-data/placement/region') 
credentials = Aws::InstanceProfileCredentials.new()
ssm_client = Aws::SSM::Client.new(
  region: region_name
)

# Option to search by runcommand:
commands = ssm_client.list_command_invocations({
    instance_id: ec2_instance_id,
 })
 
# Option to search by automation execution, and search by document name
# The document name can be seen in Application Manager
def search_by_document_name(ssm_client, document_name)    
  next_token = nil
  execution_array = []
  loop do
    executions = ssm_client.describe_automation_executions({
      filters: [
        {
          key: "DocumentNamePrefix",
          values: [document_name],
        },
      ],
      max_results: 20,
      next_token: next_token,
    })
    executions.automation_execution_metadata_list.each do |execution|
      execution_array.push(execution)
    end
    break if executions.next_token.nil?
    next_token = executions.next_token
  end

  executions_by_doc_name = []
  for automation in execution_array do 
    resp = ssm_client.get_automation_execution({
      automation_execution_id: automation['automation_execution_id'],
    })['automation_execution']
    aws_opsworks_command={
      :args => resp['step_executions'][0]['inputs'], #An Automation runbook can have a set of different parameters. The overall runbook parameter is the instance ID, see instance_id for reference. [0] here indicates which step in the runbook you are searching for for inputs.
      :command_id => automation['automation_execution_id'],
      :executed_by => resp['executed_by'],
      :instance_id => resp['parameters']['instanceId'][0],
      :sent_at => resp['execution_start_time'],
      :type => document_name,
    }
    executions_by_doc_name.push(aws_opsworks_command)
  end
  return executions_by_doc_name
end
```