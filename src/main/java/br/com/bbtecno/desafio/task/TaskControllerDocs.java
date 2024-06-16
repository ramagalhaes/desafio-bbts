package br.com.bbtecno.desafio.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Tasks", description = "Task Management API")
public interface TaskControllerDocs {

    @Operation(summary = "Get all tasks")
    @GetMapping
    Mono<ResponseEntity<List<Task>>> getTasks();

    @Operation(summary = "Get a task by ID")
    @GetMapping("/{id}")
    Mono<ResponseEntity<Task>> getTaskById(
            @Parameter(description = "ID of the task to be retrieved", required = true)
            @PathVariable String id);

    @Operation(summary = "Get tasks by status")
    @GetMapping("/status")
    Mono<ResponseEntity<List<Task>>> findByStatus(
            @Parameter(description = "Status of the tasks to be retrieved", required = true)
            @RequestParam TaskStatus status);

    @Operation(summary = "Create a new task")
    @PostMapping
    Mono<ResponseEntity<Task>> createTask(
            @Parameter(description = "Details of the task to be created", required = true)
            @RequestBody CreateTaskRequest request);

    @Operation(summary = "Update an existing task")
    @PutMapping("/{id}")
    @ApiResponse(
            description = "Successfully updated a task", responseCode = "204",
            headers = @Header(
                    name = "Location",
                    description = "location of the updated resource",
                    example = "/api/v1/tasks/example-id"
            ),
            content = @Content(examples = @ExampleObject(value = "no-content"))
    )
    @ApiResponse(description = "Task not found for the given id", responseCode = "404", content = @Content())
    Mono<ResponseEntity<Task>> update(
            @Parameter(description = "Updated details of the task", required = true)
            @RequestBody CreateTaskRequest request,
            @Parameter(description = "ID of the task to be updated", required = true)
            @PathVariable String id);

    @Operation(summary = "Delete a task")
    @DeleteMapping("/{id}")
    @ApiResponse(description = "Successfully deleted a task", responseCode = "204")
    @ApiResponse(description = "Task not found for the given id", responseCode = "404")
    Mono<ResponseEntity<Void>> delete(
            @Parameter(description = "ID of the task to be deleted", required = true)
            @PathVariable String id);
}