package bluegreen.model;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.services.ecs.model.DeploymentConfiguration;
import software.amazon.awssdk.services.ecs.model.DeploymentController;

@Data
@Builder
public class EcsFargateAlbControlDto {
    private String taskArn;
    private String region;
    private ControlTable controlTable;
    private DeploymentConfiguration deploymentConfiguration;
    private DeploymentController deploymentController;
    private String stats;
    private Integer cpu;
}
