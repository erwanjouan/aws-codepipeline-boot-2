package bluegreen.conf.dynamodb;

import bluegreen.model.exam.Domain;
import bluegreen.model.exam.Exam;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@ConfigurationProperties(prefix = "exam")
public class ExamProperties {

    private LinkedList<Domain> domains;
    private String name;
    private String id;

    public ExamProperties() {
    }

    public LinkedList<Domain> getDomains() {
        return domains;
    }

    public void setDomains(LinkedList<Domain> domains) {
        this.domains = domains;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Exam getExam() {
        return Exam.builder()
                .domains(this.domains)
                .name(this.name)
                .id(this.id)
                .build();
    }
}
