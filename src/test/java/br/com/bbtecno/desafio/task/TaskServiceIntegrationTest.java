package br.com.bbtecno.desafio.task;

import br.com.bbtecno.desafio.core.files.FileStorage;
import br.com.bbtecno.desafio.core.files.S3FileStorage;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AmazonS3 amazonS3;

    private Task task;
    private CreateTaskRequest createTaskRequest;

    @Container
    public static LocalStackContainer localStack = new LocalStackContainer()
            .withServices(LocalStackContainer.Service.S3);

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public AmazonS3 amazonS3() {
            return AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                            localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString(), "us-east-1"))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            localStack.getAccessKey(), localStack.getSecretKey())))
                    .withPathStyleAccessEnabled(true)
                    .build();
        }

        @Bean
        public S3FileStorage fileStorage(AmazonS3 amazonS3) {
            return new S3FileStorage(amazonS3);
        }
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll().block();

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test Task");
        task.setStatus(TaskStatus.TODO);
        task.setHasFile(false);

        createTaskRequest = CreateTaskRequest.builder().title("New Task").status(TaskStatus.COMPLETED).build();

        amazonS3.createBucket("test-bucket");
    }

    @Test
    void testFindAll() {
        taskRepository.save(task).block();

        Flux<Task> tasks = taskService.findAll();

        StepVerifier.create(tasks)
                .expectNextMatches(t -> t.getTitle().equals("Test Task"))
                .verifyComplete();
    }

    @Test
    void testFindAll_whenNoTasks_thenReturnEmpty() {
        Flux<Task> tasks = taskService.findAll();

        StepVerifier.create(tasks)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        taskRepository.save(task).block();

        Mono<Task> foundTask = taskService.findById(task.getId());

        StepVerifier.create(foundTask)
                .expectNextMatches(t -> t.getTitle().equals("Test Task"))
                .verifyComplete();
    }

    @Test
    void testFindById_whenTaskNotFound_thenReturnEmpty() {
        Mono<Task> foundTask = taskService.findById(UUID.randomUUID());

        StepVerifier.create(foundTask)
                .verifyComplete();
    }

    @Test
    void testCreate() {
        Mono<Task> createdTask = taskService.create(createTaskRequest);

        StepVerifier.create(createdTask)
                .expectNextMatches(t -> t.getTitle().equals("New Task") && t.getStatus() == TaskStatus.COMPLETED)
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        taskRepository.save(task).block();

        CreateTaskRequest updateRequest = CreateTaskRequest.builder().title("Updated Task").status(TaskStatus.COMPLETED).build();

        Mono<Task> updatedTask = taskService.update(task.getId(), updateRequest);

        StepVerifier.create(updatedTask)
                .expectNextMatches(t -> t.getTitle().equals("Updated Task") && t.getStatus() == TaskStatus.COMPLETED)
                .verifyComplete();
    }

    @Test
    void testUpdate_whenTaskNotFound_thenReturnEmpty() {
        CreateTaskRequest updateRequest = CreateTaskRequest.builder().title("Updated Task").status(TaskStatus.COMPLETED).build();

        Mono<Task> updatedTask = taskService.update(UUID.randomUUID(), updateRequest);

        StepVerifier.create(updatedTask)
                .verifyComplete();
    }

    @Test
    void testDelete() {
        taskRepository.save(task).block();

        Mono<Void> deleteMono = taskService.delete(task.getId());

        StepVerifier.create(deleteMono)
                .verifyComplete();

        StepVerifier.create(taskRepository.findById(task.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testDelete_whenTaskNotFound_thenReturnEmpty() {
        Mono<Void> deleteMono = taskService.delete(UUID.randomUUID());

        StepVerifier.create(deleteMono)
                .verifyComplete();
    }

    @Test
    void testFindByStatus() {
        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Another Task");
        task2.setStatus(TaskStatus.TODO);
        task2.setHasFile(false);

        taskRepository.save(task).block();
        taskRepository.save(task2).block();

        Flux<Task> tasks = taskService.findByStatus(TaskStatus.TODO);

        StepVerifier.create(tasks)
                .expectNextMatches(t -> t.getTitle().equals("Test Task"))
                .expectNextMatches(t -> t.getTitle().equals("Another Task"))
                .verifyComplete();
    }

    @Test
    void testFindByStatus_whenNoTasks_thenReturnEmpty() {
        Flux<Task> tasks = taskService.findByStatus(TaskStatus.TODO);

        StepVerifier.create(tasks)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testUploadFile() {
        taskRepository.save(task).block();

        String fileName = task.generateFileName();
        FilePart filePart = Mockito.mock(FilePart.class);

        when(filePart.filename()).thenReturn(fileName);
        when(filePart.transferTo((File) any())).thenReturn(Mono.empty());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        amazonS3.putObject("test-bucket", fileName, new ByteArrayInputStream(new byte[0]), metadata);

        Mono<Task> updatedTask = taskService.uploadFile(task.getId(), Mono.just(filePart));

        StepVerifier.create(updatedTask)
                .expectNextMatches(t -> t.getHasFile() && t.getFileName().equals(task.getId()))
                .verifyComplete();
    }

    @Test
    void testUploadFile_whenTaskNotFound_thenReturnEmpty() {
        FilePart filePart = Mockito.mock(FilePart.class);

        Mono<Task> updatedTask = taskService.uploadFile(UUID.randomUUID(), Mono.just(filePart));

        StepVerifier.create(updatedTask)
                .verifyComplete();
    }
}
