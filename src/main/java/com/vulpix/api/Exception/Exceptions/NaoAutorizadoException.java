package com.vulpix.api.Exception.Exceptions;

public class NaoAutorizadoException extends RuntimeException {
    public NaoAutorizadoException(String message) {
        super(message);
    }
}