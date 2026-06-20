package bluegreen.model.exam;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = Exam.Builder.class)
public class Exam {
    private String id;
    private String name;
    private List<Domain> domains;

    private Exam(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.domains = b.domains;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String name;
        private List<Domain> domains;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder domains(List<Domain> domains) {
            this.domains = domains;
            return this;
        }

        public Exam build() {
            return new Exam(this);
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

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam that = (Exam) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(domains, that.domains);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, domains);
    }

    @Override
    public String toString() {
        return "Exam{id=" + id + ", name=" + name + ", domains=" + domains + "}";
    }
}
