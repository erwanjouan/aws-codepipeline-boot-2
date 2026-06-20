package bluegreen.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControlTable {
    private String tableName;
    private List<String> headers;
    private List<Map<String, Object>> rows;

    private ControlTable(Builder b) {
        this.tableName = b.tableName;
        this.headers = b.headers;
        this.rows = b.rows;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tableName;
        private List<String> headers;
        private List<Map<String, Object>> rows;

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder headers(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder rows(List<Map<String, Object>> rows) {
            this.rows = rows;
            return this;
        }

        public ControlTable build() {
            return new ControlTable(this);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlTable that = (ControlTable) o;
        return Objects.equals(tableName, that.tableName)
                && Objects.equals(headers, that.headers)
                && Objects.equals(rows, that.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, headers, rows);
    }

    @Override
    public String toString() {
        return "ControlTable{tableName=" + tableName + ", headers=" + headers + ", rows=" + rows + "}";
    }
}
