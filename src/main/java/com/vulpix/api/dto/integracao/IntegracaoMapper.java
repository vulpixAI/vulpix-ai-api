package com.vulpix.api.dto.integracao;

import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;

public class IntegracaoMapper {
    public static Integracao criaEntidadeIntegracao(IntegracaoDto dto, Empresa empresa){
        if (dto == null) return null;

        Integracao integracao = Integracao.builder()
                .tipo(dto.getTipo())
                .empresa(empresa)
                .status(true)
                .build();

        return integracao;
    }

    public static Integracao criaEntidadeAtualizada(Empresa empresa, IntegracaoUpdateDto dto){
        if (dto == null) return null;

        Integracao integracao = Integracao.builder()
                .empresa(empresa)
                .clientId(dto.getClientId())
                .clientSecret(dto.getClientSecret())
                .accessToken(dto.getAccessToken())
                .igUserId(dto.getIgUserId())
                .tipo(TipoIntegracao.INSTAGRAM)
                .status(true)
                .build();

        return integracao;
    }
}
