package br.com.confidence.dto.error;

import java.time.Instant;

public record ErrorResponse(

    Instant timestamp,
    int status,
    String error,
    String message,
    String path

) {}
