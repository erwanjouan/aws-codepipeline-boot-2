package bluegreen.model;

import java.util.List;
import java.util.Objects;

public class Controls {
    private String controlName;
    private List<ControlTable> controlTableList;

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public List<ControlTable> getControlTableList() {
        return controlTableList;
    }

    public void setControlTableList(List<ControlTable> controlTableList) {
        this.controlTableList = controlTableList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Controls that = (Controls) o;
        return Objects.equals(controlName, that.controlName)
                && Objects.equals(controlTableList, that.controlTableList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlName, controlTableList);
    }

    @Override
    public String toString() {
        return "Controls{controlName=" + controlName + ", controlTableList=" + controlTableList + "}";
    }
}
