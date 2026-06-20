package bluegreen.model.exam;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = TaskStatement.Builder.class)
public class TaskStatement {
    private String id;
    private String name;
    private String parentId;
    private List<SubTask> knowledgeOf;
    private List<SubTask> skillsIn;

    public TaskStatement() {
    }

    public TaskStatement(String id, String name, String parentId, List<SubTask> knowledgeOf, List<SubTask> skillsIn) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.knowledgeOf = knowledgeOf;
        this.skillsIn = skillsIn;
    }

    private TaskStatement(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.parentId = b.parentId;
        this.knowledgeOf = b.knowledgeOf;
        this.skillsIn = b.skillsIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String name;
        private String parentId;
        private List<SubTask> knowledgeOf;
        private List<SubTask> skillsIn;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder knowledgeOf(List<SubTask> knowledgeOf) {
            this.knowledgeOf = knowledgeOf;
            return this;
        }

        public Builder skillsIn(List<SubTask> skillsIn) {
            this.skillsIn = skillsIn;
            return this;
        }

        public TaskStatement build() {
            return new TaskStatement(this);
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<SubTask> getKnowledgeOf() {
        return knowledgeOf;
    }

    public void setKnowledgeOf(List<SubTask> knowledgeOf) {
        this.knowledgeOf = knowledgeOf;
    }

    public List<SubTask> getSkillsIn() {
        return skillsIn;
    }

    public void setSkillsIn(List<SubTask> skillsIn) {
        this.skillsIn = skillsIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStatement that = (TaskStatement) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(parentId, that.parentId)
                && Objects.equals(knowledgeOf, that.knowledgeOf)
                && Objects.equals(skillsIn, that.skillsIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentId, knowledgeOf, skillsIn);
    }

    @Override
    public String toString() {
        return "TaskStatement{id=" + id + ", name=" + name + ", parentId=" + parentId + ", knowledgeOf=" + knowledgeOf + ", skillsIn=" + skillsIn + "}";
    }
}
