package com.vulpix.api.Exception;

import com.vulpix.api.Exception.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ExcecaoResponse> trataRequisicaoInvalidaException(RequisicaoInvalidaException ex) {
        return criaExcecaoResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<ExcecaoResponse> trataNaoAutorizadoException(NaoAutorizadoException ex) {
        return criaExcecaoResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ExcecaoResponse> trataNaoEncontradoException(NaoEncontradoException ex) {
        return criaExcecaoResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ExcecaoResponse> trataConflitoException(ConflitoException ex) {
        return criaExcecaoResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EntidadeNaoProcessavelException.class)
    public ResponseEntity<ExcecaoResponse> trataEntidadeNaoProcessavelException(EntidadeNaoProcessavelException ex) {
        return criaExcecaoResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    private ResponseEntity<ExcecaoResponse> criaExcecaoResponse(HttpStatus status, String detail) {
        ExcecaoResponse excecaoResponse = new ExcecaoResponse(status.value(), detail, LocalDateTime.now());
        return new ResponseEntity<>(excecaoResponse, status);
    }
}