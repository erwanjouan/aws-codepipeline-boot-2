package bluegreen.model;

import lombok.Data;

import java.util.List;

@Data
public class Controls {
    private String controlName;
    private List<ControlTable> controlTableList;
}
