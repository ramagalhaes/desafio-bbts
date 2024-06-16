package br.com.bbtecno.desafio.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public Flux<Task> findAll() {
        return taskRepository.findAll();
    }

    public Mono<Task> findById(String id) {
        return taskRepository.findById(id)
                .doOnNext(v -> log.info(String.format("TaskService::findById() - task found: {%s}", v)));
    }

    public Mono<Task> create(CreateTaskRequest request) {
        return taskRepository.save(Task.fromRequest(request));
    }

    public Mono<Task> update(String id, CreateTaskRequest task) {
        return taskRepository
                .findById(id)
                .flatMap(t -> taskRepository.save(t.update(Task.fromRequest(task))));
    }

    public Mono<Void> delete(String id) {
        return taskRepository.deleteById(id);
    }

    public Flux<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

}
