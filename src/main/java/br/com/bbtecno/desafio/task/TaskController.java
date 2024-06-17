package br.com.bbtecno.desafio.task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

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
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable UUID id) {
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
    public Mono<ResponseEntity<Object>> createTask(@RequestBody CreateTaskRequest request) {
        return taskService.create(request)
                .map(createdTask -> ResponseEntity.created(UriComponentsBuilder.fromPath("/api/v1/tasks/{id}").buildAndExpand(createdTask.getId()).toUri())
                        .build())
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> update(@RequestBody CreateTaskRequest request, @PathVariable UUID id) {
        return taskService.update(id, request)
                .map(createdTask -> ResponseEntity.created(UriComponentsBuilder.fromPath("/api/v1/tasks/{id}").buildAndExpand(createdTask.getId()).toUri())
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        return taskService.delete(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @PutMapping("/file-upload/{id}")
    public Mono<ResponseEntity<Object>> uploadFile(@PathVariable UUID id, @RequestPart("file") Mono<FilePart> filePart) {
        return taskService.uploadFile(id, filePart)
                .map(createdTask -> ResponseEntity.created(UriComponentsBuilder.fromPath("/api/v1/tasks/{id}").buildAndExpand(createdTask.getId()).toUri())
                        .build())
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

}
