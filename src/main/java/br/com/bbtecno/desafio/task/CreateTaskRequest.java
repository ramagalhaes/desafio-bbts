package br.com.bbtecno.desafio.task;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public record CreateTaskRequest(
        String id,
        String title,
        String filePath,
        TaskStatus status
) {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
