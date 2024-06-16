package br.com.bbtecno.desafio.task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController implements TaskControllerDocs {

    private final TaskService taskService;

    @GetMapping
    public Mono<ResponseEntity<List<Task>>> getTasks() {
        return taskService.findAll()
                .collectList()
                .flatMap(elements -> Mono.justOrEmpty(ResponseEntity.ok(elements)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable String id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/status")
    public Mono<ResponseEntity<List<Task>>> findByStatus(@RequestParam TaskStatus status) {
        return taskService.findByStatus(status)
                .collectList()
                .flatMap(elements -> Mono.justOrEmpty(ResponseEntity.ok(elements)));
    }

    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@RequestBody CreateTaskRequest request) {
        return taskService.create(request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> update(@RequestBody CreateTaskRequest request, @PathVariable String id) {
//        return request.map(r -> taskService.update(id, r))
//                .flatMap(created -> Mono.just(ResponseEntity
//                        .created(UriComponentsBuilder.fromPath("/api/v1/tasks/{id}").buildAndExpand(created.map(Task::getId)).toUri())
//                        .build())
//                );
        return taskService.update(id, request)
                .map(createdTask -> ResponseEntity.created(UriComponentsBuilder.fromPath("/api/v1/tasks/{id}").buildAndExpand(createdTask.getId()).toUri())
                .build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return taskService.delete(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

}
