package br.com.bbtecno.desafio.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import br.com.bbtecno.desafio.core.files.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private UUID taskId;
    private CreateTaskRequest createTaskRequest;
    private FilePart filePart;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        createTaskRequest = new CreateTaskRequest("Test task", TaskStatus.TODO, false);
        task = new Task(taskId, "Test task", null, TaskStatus.TODO, null);
        filePart = Mockito.mock(FilePart.class);
    }

    @Test
    void shouldFindListOfTasks() {
        when(taskRepository.findAll()).thenReturn(Flux.just(task));

        Flux<Task> tasks = taskService.findAll();

        StepVerifier.create(tasks)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void shouldFindTaskById() {
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(task));

        Mono<Task> foundTask = taskService.findById(taskId);

        StepVerifier.create(foundTask)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void shouldCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        Mono<Task> createdTask = taskService.create(createTaskRequest);

        StepVerifier.create(createdTask)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask() {
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        Mono<Task> updatedTask = taskService.update(taskId, createTaskRequest);

        StepVerifier.create(updatedTask)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskAndFile_WhenFileExists() {
        Task deleteableTask = new Task(taskId, "Test task", taskId.toString(), TaskStatus.TODO, true);
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(deleteableTask));
        when(fileStorage.delete(deleteableTask.generateFileName())).thenReturn(Mono.empty());
        when(taskRepository.delete(deleteableTask)).thenReturn(Mono.empty());

        Mono<Void> result = taskService.delete(deleteableTask.getId());

        StepVerifier.create(result).verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
        verify(fileStorage, times(1)).delete(deleteableTask.generateFileName());
        verify(taskRepository, times(1)).delete(deleteableTask);
    }

    @Test
    void shouldOnlyDeleteTask_WhenFileDeoesntExists() {
        Task deleteableTask = new Task(taskId, "Test task", null, TaskStatus.TODO, false);
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(deleteableTask));
        when(taskRepository.delete(deleteableTask)).thenReturn(Mono.empty());

        Mono<Void> result = taskService.delete(deleteableTask.getId());

        StepVerifier.create(result).verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
        verify(fileStorage, times(0)).delete(deleteableTask.generateFileName());
        verify(taskRepository, times(1)).delete(deleteableTask);
    }

    @Test
    void shouldFindListOfTasks_WhenStatusIsPresentInDB() {
        TaskStatus status = TaskStatus.DOING;
        when(taskRepository.findByStatus(status)).thenReturn(Flux.just(task));

        Flux<Task> tasks = taskService.findByStatus(status);

        StepVerifier.create(tasks)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).findByStatus(status);
    }

    @Test
    void shouldUploadFile() {
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(task));
        when(fileStorage.save(any(Mono.class), any(String.class))).thenReturn(Mono.just("savedFilePath"));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        Mono<Task> updatedTask = taskService.uploadFile(taskId, Mono.just(filePart));

        StepVerifier.create(updatedTask)
                .expectNext(task)
                .verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
        verify(fileStorage, times(1)).save(any(Mono.class), any(String.class));
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}

