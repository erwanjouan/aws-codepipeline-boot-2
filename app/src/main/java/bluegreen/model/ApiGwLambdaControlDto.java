package bluegreen.model;

import com.amazonaws.services.lambda.runtime.Context;


import java.util.Objects;

public class ApiGwLambdaControlDto {
    private String functionName;
    private Context context;
    private ControlTable controlTable;

    private ApiGwLambdaControlDto(Builder b) {
        this.functionName = b.functionName;
        this.context = b.context;
        this.controlTable = b.controlTable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String functionName;
        private Context context;
        private ControlTable controlTable;

        public Builder functionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public ApiGwLambdaControlDto build() {
            return new ApiGwLambdaControlDto(this);
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
        ApiGwLambdaControlDto that = (ApiGwLambdaControlDto) o;
        return Objects.equals(functionName, that.functionName)
                && Objects.equals(context, that.context)
                && Objects.equals(controlTable, that.controlTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, context, controlTable);
    }

    @Override
    public String toString() {
        return "ApiGwLambdaControlDto{functionName=" + functionName + ", context=" + context + ", controlTable=" + controlTable + "}";
    }
}
