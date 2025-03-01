package com.vulpix.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExcecaoResponse {
    private Integer status;
    private String detail;
    private LocalDateTime timestamp;
}