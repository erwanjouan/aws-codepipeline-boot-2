package bluegreen.model;

import java.util.Objects;

public class AppRunnerControlDto {
    private ControlTable controlTable;

    private AppRunnerControlDto(Builder b) {
        this.controlTable = b.controlTable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ControlTable controlTable;

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public AppRunnerControlDto build() {
            return new AppRunnerControlDto(this);
        }
    }

    public ControlTable getControlTable() {
        return controlTable;
    }

    public void setControlTable(ControlTable controlTable) {
        this.controlTable = controlTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppRunnerControlDto that = (AppRunnerControlDto) o;
        return Objects.equals(controlTable, that.controlTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlTable);
    }

    @Override
    public String toString() {
        return "AppRunnerControlDto{controlTable=" + controlTable + "}";
    }
}
