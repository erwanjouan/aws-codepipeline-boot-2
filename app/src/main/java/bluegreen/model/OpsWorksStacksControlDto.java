package bluegreen.model;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

@Data
@Builder
public class OpsWorksStacksControlDto {
    private String functionName;
    private EC2MetadataUtils.InstanceInfo instanceInfo;
    private ControlTable controlTable;
}
