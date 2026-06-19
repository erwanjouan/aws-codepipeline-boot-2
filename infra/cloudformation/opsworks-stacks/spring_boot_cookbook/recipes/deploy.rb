execute 'download_artifact_from_s3' do
    command "aws s3 cp s3://#{node['artifact_bucket']}/#{node['git_sha1']}/app.jar /tmp/app_#{node['git_sha1']}.jar"
end

execute 'kill_previous_blue_green_app' do
    command "cat /tmp/blue-green-app-pid.txt | xargs kill -9 || true"
end

execute 'execute_spring_boot_jar' do
    command "SPRING_PROFILES_ACTIVE=#{node['spring_profiles_active']} java -jar /tmp/app_#{node['git_sha1']}.jar & echo $! > /tmp/blue-green-app-pid.txt"
end