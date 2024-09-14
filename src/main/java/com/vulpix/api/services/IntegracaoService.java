package com.vulpix.api.services;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class IntegracaoService {
    @Autowired
    private IntegracaoRepository integracaoRepository;

    public Optional<Integracao> getIntegracaoById(UUID id) {
        return integracaoRepository.findById(id);
    }

    public LocalDateTime verificarAccessToken(UUID integracaoId) {
        Integracao integracao = getIntegracaoById(integracaoId).get();
        LocalDateTime expiraEm = integracao.getAccessTokenExpireDate();

        return expiraEm;
    }
}
