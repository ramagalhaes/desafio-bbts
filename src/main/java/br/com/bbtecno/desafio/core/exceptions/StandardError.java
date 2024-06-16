package br.com.bbtecno.desafio.core.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record StandardError(
        LocalDateTime timestamp,
        Integer httpStatus,
        String message,
        String path
) {
}