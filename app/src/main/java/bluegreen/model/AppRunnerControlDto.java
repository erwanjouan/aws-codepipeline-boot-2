package bluegreen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppRunnerControlDto {
    private ControlTable controlTable;
}
