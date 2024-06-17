package br.com.bbtecno.desafio.task;

import br.com.bbtecno.desafio.core.files.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final FileStorage fileStorage;

    public Flux<Task> findAll() {
        return taskRepository.findAll();
    }

    public Mono<Task> findById(UUID id) {
        return taskRepository.findById(id)
                .doOnNext(v -> log.info(String.format("TaskService::findById() - task found: {%s}", v)));
    }

    public Mono<Task> create(CreateTaskRequest request) {
        UUID id = UUID.randomUUID();
        return taskRepository.save(Task.fromRequest(request, id));
    }

    public Mono<Task> update(UUID id, CreateTaskRequest request) {
        return taskRepository
                .findById(id)
                .flatMap(t -> taskRepository.save(t.update(Task.fromRequest(request, id))));
    }

    public Mono<Void> delete(UUID id) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    Mono<String> deleteFileMono = Mono.empty();
                    if (task.getHasFile() != null && task.getHasFile().equals(TRUE)) {
                        deleteFileMono = fileStorage.delete(task.generateFileName());
                    }
                    return deleteFileMono.then(taskRepository.delete(task));
                });
    }

    public Flux<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Mono<Task> uploadFile(UUID id, Mono<FilePart> filePart) {
        return taskRepository.findById(id)
                .flatMap(task -> filePart
                        .flatMap(file -> fileStorage.save(Mono.just(file), task.generateFileName()))
                        .flatMap(savedFilePath -> {
                            task.setHasFile(true);
                            task.setFileName(id.toString());
                            return taskRepository.save(task);
                        })
                );
    }

}
