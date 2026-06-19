package bluegreen.dao.dynamodb;

import bluegreen.conf.dynamodb.ExamProperties;
import bluegreen.model.exam.Domain;
import bluegreen.model.exam.Exam;
import bluegreen.model.exam.SubTask;
import bluegreen.model.exam.TaskStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.DOMAINS_FILE;
import static bluegreen.model.Constant.DOMAINS_TABLE;
import static bluegreen.model.Constant.GSI_PARENT_INDEX_NAME;
import static bluegreen.model.Constant.ID;
import static bluegreen.model.Constant.KNOWLEDGES_OF_FILE;
import static bluegreen.model.Constant.KNOWLEDGES_OF_TABLE;
import static bluegreen.model.Constant.NAME;
import static bluegreen.model.Constant.PARENT;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;
import static bluegreen.model.Constant.SKILLS_IN_FILE;
import static bluegreen.model.Constant.SKILLS_IN_TABLE;
import static bluegreen.model.Constant.TASK_STATEMENTS_FILE;
import static bluegreen.model.Constant.TASK_STATEMENTS_TABLE;

public class DynamoDbDaoImpl implements DynamoDbDao {

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExamProperties examProperties;

    @Override
    public Exam getExam() {
        final String examId = this.examProperties.getId();
        final String examName = this.examProperties.getName();
        // Domains
        final List<Domain> domains = this.getDomainsFromGsi(examId);
        // TaskStatement
        final List<TaskStatement> taskStatements = this.getTaskStatementsFromGsi(domains);
        // KnowledgeOf
        this.getKnowledgeOfsFromGsi(taskStatements);
        // SkillsIn
        this.getSkillsInFromGsi(taskStatements);

        return Exam.builder()
                .id(examId)
                .name(examName)
                .domains(domains)
                .build();
    }

    private void getKnowledgeOfs(final TaskStatement taskStatement,
                                 final List<Map<String, AttributeValue>> savedItems) {
        final String taskStatementId = taskStatement.getId();
        final QueryResponse query = this.dynamoDbClient.query(QueryRequest.builder()
                .tableName(KNOWLEDGES_OF_TABLE)
                .indexName(GSI_PARENT_INDEX_NAME)
                .keyConditionExpression("Parent = :ParentValue")
                .expressionAttributeValues(Map.of(":ParentValue", AttributeValue.builder()
                        .s(taskStatementId)
                        .build()))
                .build());
        final List<Map<String, AttributeValue>> items = query.items();
        savedItems.addAll(items);
        items.stream()
                .map(this::toSubTask)
                .forEach(subTask -> taskStatement.getKnowledgeOf().add(subTask));
    }

    private void getSkillsIn(final TaskStatement taskStatement,
                             final List<Map<String, AttributeValue>> savedItems) {
        final String taskStatementId = taskStatement.getId();
        final QueryResponse query = this.dynamoDbClient.query(QueryRequest.builder()
                .tableName(SKILLS_IN_TABLE)
                .indexName(GSI_PARENT_INDEX_NAME)
                .keyConditionExpression("Parent = :ParentValue")
                .expressionAttributeValues(Map.of(":ParentValue", AttributeValue.builder()
                        .s(taskStatementId)
                        .build()))
                .build());
        final List<Map<String, AttributeValue>> items = query.items();
        savedItems.addAll(items);
        items.stream()
                .map(this::toSubTask)
                .forEach(subTask -> taskStatement.getSkillsIn().add(subTask));
    }

    private List<Domain> getDomainsFromGsi(final String examId) {
        final QueryResponse response = this.dynamoDbClient.query(QueryRequest.builder()
                .tableName(DOMAINS_TABLE)
                .indexName(GSI_PARENT_INDEX_NAME)
                .keyConditionExpression("Parent = :ParentValue")
                .expressionAttributeValues(Map.of(":ParentValue", AttributeValue.builder()
                        .s(examId)
                        .build()))
                .build());

        final List<Map<String, AttributeValue>> items = response.items();

        this.saveToDisk(items, DOMAINS_FILE);

        return items.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private List<TaskStatement> getTaskStatementsFromGsi(final List<Domain> domains) {
        final List<Map<String, AttributeValue>> savedItems = new ArrayList<>();
        final List<TaskStatement> taskStatements = domains.stream()
                .map(domain -> this.getTaskStatements(domain, savedItems))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        this.saveToDisk(savedItems, TASK_STATEMENTS_FILE);
        return taskStatements;
    }

    private void getKnowledgeOfsFromGsi(final List<TaskStatement> taskStatements) {
        final List<Map<String, AttributeValue>> savedItems = new ArrayList<>();
        taskStatements.forEach(taskStatement -> this.getKnowledgeOfs(taskStatement, savedItems));
        this.saveToDisk(savedItems, KNOWLEDGES_OF_FILE);
    }

    private void getSkillsInFromGsi(final List<TaskStatement> taskStatements) {
        final List<Map<String, AttributeValue>> savedItems = new ArrayList<>();
        taskStatements.forEach(taskStatement -> this.getSkillsIn(taskStatement, savedItems));
        this.saveToDisk(savedItems, SKILLS_IN_FILE);
    }

    private List<TaskStatement> getTaskStatements(final Domain domain,
                                                  final List<Map<String, AttributeValue>> savedItems) {
        final String domainId = domain.getId();
        final QueryResponse query = this.dynamoDbClient.query(QueryRequest.builder()
                .tableName(TASK_STATEMENTS_TABLE)
                .indexName(GSI_PARENT_INDEX_NAME)
                .keyConditionExpression("Parent = :ParentValue")
                .expressionAttributeValues(Map.of(":ParentValue", AttributeValue.builder()
                        .s(domainId)
                        .build()))
                .build());
        final List<Map<String, AttributeValue>> items = query.items();

        savedItems.addAll(items);

        return items.stream()
                .map(this::toTaskStatement)
                .map(taskStatement -> {
                    domain.getTaskStatements().add(taskStatement);
                    return taskStatement;
                })
                .collect(Collectors.toList());
    }

    private void saveToDisk(final List<Map<String, AttributeValue>> items, final String fileName) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            items.stream()
                    .sorted(Comparator.comparing(item -> this.sanitize((Map<String, AttributeValue>) item, ID)))
                    .forEach(item -> {
                        stringBuilder.append("{\"Item\":{");
                        stringBuilder.append(this.encodeStringMapAttribute(item));
                        stringBuilder.append("}}\n");
                    });
            final Path importFile = Path.of("dynamodb-lambda", fileName);
            Files.writeString(importFile, stringBuilder.toString());
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String encodeStringMapAttribute(final Map<String, AttributeValue> item) {
        return List.of(ID, NAME, PARENT)
                .stream()
                .filter(col -> Objects.nonNull(item.get(col)))
                .map(col -> String.format("\"%s\":{\"S\": \"%s\"}", col, this.sanitize(item, col)))
                .collect(Collectors.joining(","));
    }

    private String sanitize(final Map<String, AttributeValue> item, final String col) {
        return Optional.ofNullable(item.get(col))
                .map(AttributeValue::s)
                .orElse(null);
    }

    private Domain toDomain(final Map<String, AttributeValue> stringAttributeValueMap) {
        return Domain.builder()
                .id(this.sanitize(stringAttributeValueMap, ID))
                .name(this.sanitize(stringAttributeValueMap, NAME))
                .taskStatements(new ArrayList<>())
                .build();
    }

    private TaskStatement toTaskStatement(final Map<String, AttributeValue> stringAttributeValueMap) {
        return TaskStatement.builder()
                .id(this.sanitize(stringAttributeValueMap, ID))
                .name(this.sanitize(stringAttributeValueMap, NAME))
                .parentId(this.sanitize(stringAttributeValueMap, PARENT))
                .knowledgeOf(new ArrayList<>())
                .skillsIn(new ArrayList<>())
                .build();
    }

    private SubTask toSubTask(final Map<String, AttributeValue> stringAttributeValueMap) {
        return SubTask.builder()
                .id(this.sanitize(stringAttributeValueMap, ID))
                .name(this.sanitize(stringAttributeValueMap, NAME))
                .parentId(this.sanitize(stringAttributeValueMap, PARENT))
                .build();
    }
}
