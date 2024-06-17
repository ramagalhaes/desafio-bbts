package br.com.bbtecno.desafio.task;

import lombok.Builder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Builder
public record CreateTaskRequest(
        String title,
        TaskStatus status,
        Boolean hasFile
) {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
