package com.blog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExceptionResponse {
    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime errorTime;
    private String errorPath;
    private String errorMessage;
    private Integer errorStatusCode;
}
