package br.com.bbtecno.desafio.task;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Task {

    @Id
    private UUID id;
    private String title;
    private String fileName;
    private TaskStatus status = TaskStatus.TODO;
    private Boolean hasFile = false;

    public Task update(Task task) {
        this.title = task.getTitle() == null ? title : task.getTitle();
        this.fileName = task.getFileName() == null ? fileName : task.getFileName();
        this.status = task.getStatus() == null ? status : task.getStatus();
        return this;
    }

    public static Task fromRequest(CreateTaskRequest request, UUID uuid) {
        return Task.builder()
                .id(uuid)
                .title(request.title())
                .hasFile(request.hasFile() != null && request.hasFile())
                .status(request.status() == null ? TaskStatus.TODO : request.status())
                .build();
    }

    public String generateFileName() {
        return id.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
