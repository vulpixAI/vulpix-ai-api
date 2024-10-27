package com.vulpix.api.dto.Integracao.Resquest;

import com.vulpix.api.Utils.Enum.TipoIntegracao;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;

public class IntegracaoMapper {
    public static Integracao criaEntidadeIntegracao(com.vulpix.api.dto.Integracao.Resquest.IntegracaoDto dto, Empresa empresa){
        if (dto == null) return null;

        Integracao integracao = Integracao.builder()
                .tipo(dto.getTipo())
                .empresa(empresa)
                .status(true)
                .build();

        return integracao;
    }

    public static Integracao criaEntidadeAtualizada(Empresa empresa, com.vulpix.api.dto.Integracao.Resquest.IntegracaoUpdateDto dto){
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
