package bluegreen.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ControlTable {
    private String tableName;
    private List<String> headers;
    private List<Map<String, Object>> rows;
}
