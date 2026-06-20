package bluegreen.model.exam;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = Domain.Builder.class)
public class Domain {
    private String id;
    private String name;
    private List<TaskStatement> taskStatements;

    public Domain() {
    }

    public Domain(String id, String name, List<TaskStatement> taskStatements) {
        this.id = id;
        this.name = name;
        this.taskStatements = taskStatements;
    }

    private Domain(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.taskStatements = b.taskStatements;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String name;
        private List<TaskStatement> taskStatements;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder taskStatements(List<TaskStatement> taskStatements) {
            this.taskStatements = taskStatements;
            return this;
        }

        public Domain build() {
            return new Domain(this);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskStatement> getTaskStatements() {
        return taskStatements;
    }

    public void setTaskStatements(List<TaskStatement> taskStatements) {
        this.taskStatements = taskStatements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain that = (Domain) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(taskStatements, that.taskStatements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskStatements);
    }

    @Override
    public String toString() {
        return "Domain{id=" + id + ", name=" + name + ", taskStatements=" + taskStatements + "}";
    }
}
