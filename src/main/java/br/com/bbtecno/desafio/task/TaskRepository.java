package br.com.bbtecno.desafio.task;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TaskRepository extends ReactiveMongoRepository<Task, UUID> {
    Flux<Task> findByStatus(TaskStatus status);
}
