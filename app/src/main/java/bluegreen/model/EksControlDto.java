package bluegreen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EksControlDto {
    private String podName;
    private String region;
    private ControlTable controlTable;
    private Integer cpu;
}
