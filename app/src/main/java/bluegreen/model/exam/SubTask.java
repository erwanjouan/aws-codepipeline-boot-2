package bluegreen.model.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@AllArgsConstructor // needed for @ConfigurationProperties
@NoArgsConstructor // needed for @ConfigurationProperties
public class SubTask {
    private String id;
    private String name;
    private String parentId;
}
