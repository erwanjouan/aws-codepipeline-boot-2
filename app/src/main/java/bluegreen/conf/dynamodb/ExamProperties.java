package bluegreen.conf.dynamodb;

import bluegreen.model.exam.Domain;
import bluegreen.model.exam.Exam;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Data
@Component
@ConfigurationProperties(prefix = "exam")
public class ExamProperties {

    private LinkedList<Domain> domains;
    private String name;
    private String id;

    public Exam getExam() {
        return Exam.builder()
                .domains(this.domains)
                .name(this.name)
                .id(this.id)
                .build();
    }
}
