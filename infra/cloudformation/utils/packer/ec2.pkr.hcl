packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "amazon-linux2" {
  ami_name = "amazon-linux2-ami-with-agents"
  instance_type = "t2.micro"
  region = "eu-west-1"
  source_ami_filter {
    filters = {
      name = "*amzn2-ami-hvm-*"
      root-device-type = "ebs"
      virtualization-type = "hvm"
    }
    most_recent = true
    owners = [
      "amazon"]
  }
  ssh_username = "ec2-user"
}

build {
  sources = [
    "source.amazon-ebs.amazon-linux2"
  ]

  provisioner "ansible" {
    playbook_file = "./playbook.yml"
  }
}