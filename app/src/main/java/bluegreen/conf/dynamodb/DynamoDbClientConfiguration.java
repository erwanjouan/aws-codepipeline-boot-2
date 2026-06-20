package bluegreen.conf.dynamodb;

import bluegreen.dao.dynamodb.DynamoDbDao;
import bluegreen.dao.dynamodb.DynamoDbDaoImpl;
import bluegreen.model.exam.SubTask;
import bluegreen.model.exam.TaskStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import static bluegreen.model.Constant.DOMAINS_TABLE;
import static bluegreen.model.Constant.DYNAMODB_LAMBDA_PROFILE;
import static bluegreen.model.Constant.GSI_PARENT_INDEX_NAME;
import static bluegreen.model.Constant.ID;
import static bluegreen.model.Constant.KNOWLEDGES_OF_TABLE;
import static bluegreen.model.Constant.NAME;
import static bluegreen.model.Constant.PARENT;
import static bluegreen.model.Constant.SKILLS_IN_TABLE;
import static bluegreen.model.Constant.TASK_STATEMENTS_TABLE;

@Configuration
@Profile(DYNAMODB_LAMBDA_PROFILE)
public class DynamoDbClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbClientConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ExamProperties examProperties;

    @Bean
    @Profile("local")
    DynamoDbClient getLocalDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .endpointOverride(URI.create("http://localhost:8000"))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(20))
                        .build())
                .build();
    }

    @Bean
    @Profile("!local")
    DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .build();
    }

    @Bean
    @Profile("local")
    DynamoDbDao dynamoDbDaoLocal() {
        final DynamoDbClient localDynamoDbClient = this.getLocalDynamoDbClient();
        try {
            this.createTable(localDynamoDbClient, DOMAINS_TABLE, NAME, ID);
            this.createTable(localDynamoDbClient, TASK_STATEMENTS_TABLE, NAME, ID);
            this.createTable(localDynamoDbClient, KNOWLEDGES_OF_TABLE, NAME, ID);
            this.createTable(localDynamoDbClient, SKILLS_IN_TABLE, NAME, ID);
            this.populateTables(localDynamoDbClient);
        } catch (final Exception exception) {
            log.error("Cannot reach local dynamodb instance, launch docker-compose first", exception);
            System.exit(1);
        }
        return new DynamoDbDaoImpl();
    }

    @Bean
    @Profile("!local")
    DynamoDbDao dynamoDbDaoAws() {
        return new DynamoDbDaoImpl();
    }

    private void populateTables(final DynamoDbClient localDynamoDbClient) {
        final String examId = this.examProperties.getId();
        this.examProperties.getDomains().forEach(domain -> {
            try {
                localDynamoDbClient.putItem(
                        PutItemRequest.builder()
                                .tableName(DOMAINS_TABLE)
                                .item(
                                        Map.of(
                                                ID, AttributeValue.builder().s(domain.getId()).build(),
                                                NAME, AttributeValue.builder().s(domain.getName()).build(),
                                                PARENT, AttributeValue.builder().s(examId).build()
                                        )
                                )
                                .build()
                );
                domain.getTaskStatements().forEach(taskStatement -> {
                    localDynamoDbClient.putItem(
                            PutItemRequest.builder()
                                    .tableName(TASK_STATEMENTS_TABLE)
                                    .item(
                                            Map.of(
                                                    ID, AttributeValue.builder().s(taskStatement.getId()).build(),
                                                    NAME, AttributeValue.builder().s(taskStatement.getName()).build(),
                                                    PARENT, AttributeValue.builder().s(domain.getId()).build()
                                            )
                                    )
                                    .build()
                    );
                    for (int i = 0; i < taskStatement.getKnowledgeOf().size(); i++) {
                        final SubTask subTask = taskStatement.getKnowledgeOf().get(i);
                        subTask.setId(String.format("%s.K.%d", taskStatement.getId(), i + 1));
                        this.putSubTask(localDynamoDbClient, KNOWLEDGES_OF_TABLE, taskStatement, subTask);
                    }
                    for (int i = 0; i < taskStatement.getSkillsIn().size(); i++) {
                        final SubTask subTask = taskStatement.getSkillsIn().get(i);
                        subTask.setId(String.format("%s.S.%d", taskStatement.getId(), i + 1));
                        this.putSubTask(localDynamoDbClient, SKILLS_IN_TABLE, taskStatement, subTask);
                    }
                });
            } catch (final SdkException sdkException) {
                log.error("Error populating table {} {}", sdkException);
            }
        });
    }

    private void putSubTask(final DynamoDbClient localDynamoDbClient, final String tableName, final TaskStatement taskStatement, final SubTask subTask) {
        localDynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(
                                Map.of(
                                        ID, AttributeValue.builder().s(subTask.getId()).build(),
                                        NAME, AttributeValue.builder().s(subTask.getName()).build(),
                                        PARENT, AttributeValue.builder().s(taskStatement.getId()).build()
                                )
                        )
                        .build()
        );
    }

    private void createTable(final DynamoDbClient localDynamoDbClient, final String tableName,
                             final String partitionKey, final String sortKey) {
        try {
            localDynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName(partitionKey).attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName(sortKey).attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName(PARENT).attributeType(ScalarAttributeType.S).build()
                    )
                    .keySchema(
                            KeySchemaElement.builder().attributeName(partitionKey).keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName(sortKey).keyType(KeyType.RANGE).build()
                    )
                    .globalSecondaryIndexes(
                            GlobalSecondaryIndex.builder()
                                    .indexName(GSI_PARENT_INDEX_NAME)
                                    .keySchema(
                                            KeySchemaElement.builder().attributeName(PARENT).keyType(KeyType.HASH).build(),
                                            KeySchemaElement.builder().attributeName(sortKey).keyType(KeyType.RANGE).build()
                                    )
                                    .projection(Projection.builder()
                                            .projectionType(ProjectionType.ALL)
                                            .build())
                                    .build()
                    )
                    .build());
            log.info("Table {} created", tableName);
        } catch (final ResourceInUseException resourceInUseException) {
            log.info("Table {} already created", tableName);
        }
    }
}
