package br.com.bbtecno.desafio.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TaskStatus {

    TODO("TODO", 1),
    DOING("DOING", 2),
    COMPLETED("COMPLETED", 3);

    private final String value;
    private final Integer code;

    public static TaskStatus fromCode(int code) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getCode().equals(code)) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("Task status not found");
    }

    public static TaskStatus fromValue(String value) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getValue().equals(value)) {
                return taskStatus;
            }
        }
        return TODO;
    }
}
