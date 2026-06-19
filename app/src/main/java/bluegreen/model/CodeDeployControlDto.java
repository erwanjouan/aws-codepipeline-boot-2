package bluegreen.model;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

@Data
@Builder
public class CodeDeployControlDto {
    private EC2MetadataUtils.InstanceInfo instanceInfo;
    private ControlTable controlTable;
    private String deploymentConfigName;
    private String deploymentType;
    private String deploymentOption;

}
