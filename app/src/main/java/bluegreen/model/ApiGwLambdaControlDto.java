package bluegreen.model;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiGwLambdaControlDto {
    private String functionName;
    private Context context;
    private ControlTable controlTable;
}
