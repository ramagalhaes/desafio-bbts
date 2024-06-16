package br.com.bbtecno.desafio.task;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Task {

    @Id
    private String id = UUID.randomUUID().toString();
    private String title;
    private String filePath;
    private TaskStatus status = TaskStatus.TODO;

    public Task update(Task task) {
        this.title = task.getTitle();
        this.filePath = task.getFilePath();
        this.status = task.getStatus();
        return this;
    }

    public static Task fromRequest(CreateTaskRequest request) {
        return Task.builder()
                .id(request.id() == null ? UUID.randomUUID().toString() : request.id())
                .title(request.title())
                .filePath(request.filePath())
                .status(request.status() == null ? TaskStatus.TODO : request.status())
                .build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
