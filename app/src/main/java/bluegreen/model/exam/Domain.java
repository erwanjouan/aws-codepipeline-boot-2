package bluegreen.model.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@AllArgsConstructor // needed for @ConfigurationProperties
@NoArgsConstructor // needed for @ConfigurationProperties
@Jacksonized
public class Domain {
    private String id;
    private String name;
    private List<TaskStatement> taskStatements;
}
