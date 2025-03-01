package com.vulpix.api.utils.enums;

public enum StatusPublicacao {
    AGENDADA(0),
    PUBLICADA(1);

    private final int code;

    StatusPublicacao(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
