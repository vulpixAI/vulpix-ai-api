package com.vulpix.api.services.integracoes;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class Graph {
    private final String BASE_URL_TOKEN = "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id";
    @Autowired
    private IntegracaoRepository integracaoRepository;
    public boolean renovarAccessToken(UUID empresaId){
        Optional<Integracao> integracaoEmpresa = integracaoRepository.findIntegracaoByEmpresaId(empresaId);
        if (integracaoEmpresa.isEmpty()) return false;

        // Implementar requisição externa para atualizar token
        return true;
    }
}
