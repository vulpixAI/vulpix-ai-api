package com.vulpix.api.dto.Agent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicacaoGeradaRetorno {
    private String legenda;

    private String imagem1;
    private String imagem2;
    private String imagem3;
    private String imagem4;
}
