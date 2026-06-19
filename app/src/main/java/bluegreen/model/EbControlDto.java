package bluegreen.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EbControlDto {
    private InstanceInfo instanceInfo;
    private Map<String, Object> deploymentInfo;
    private ControlTable controlTable;
    private String platformType;
}
