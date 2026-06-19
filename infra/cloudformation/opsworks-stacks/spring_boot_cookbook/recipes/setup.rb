package 'java-17-amazon-corretto-headless' do
    action :install
end

package 'unzip' do
    action :install
end

remote_file "/tmp/awscliv2.zip" do
    source "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip"
    action :create_if_missing
end

execute 'extract_awscliv2.zip' do
    command 'unzip /tmp/awscliv2.zip -d /tmp'
end

execute 'awscliv2_install' do
  command '/tmp/aws/install'
end
