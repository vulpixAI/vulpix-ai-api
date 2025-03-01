package com.vulpix.api.utils.enums;

public enum StatusUsuario {
    AGUARDANDO_PAGAMENTO(0),
    AGUARDANDO_FORMULARIO(1),
    CADASTRO_FINALIZADO(2),
    PAGAMENTO_EM_ATRASO(3);

    private final int code;

    StatusUsuario(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
