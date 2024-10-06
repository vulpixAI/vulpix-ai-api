package com.vulpix.api.dto.Integracao.Req;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;

public class IntegracaoMapper {
    public static Integracao criaEntidadeIntegracao(IntegracaoDto dto, Empresa empresa){
        if (dto == null) return null;

        Integracao integracao = Integracao.builder()
                .tipo(dto.getTipo())
                .empresa(empresa)
                .build();

        return integracao;
    }
}
