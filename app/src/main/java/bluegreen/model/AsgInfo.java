package bluegreen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsgInfo {

    private String instanceId;
    private String asgName;
    private String healthStatus;
    private String lifecycleState;
    private String lcVersion;
    private String launchTemplateVersion;

    public final static AsgInfo DEFAULT_ASG_INFO = AsgInfo.builder()
            .asgName("")
            .lifecycleState("")
            .build();
}
