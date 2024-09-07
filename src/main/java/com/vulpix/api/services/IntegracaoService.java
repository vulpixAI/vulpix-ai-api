package com.vulpix.api.services;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

public class IntegracaoService {
    @Autowired
    private IntegracaoRepository integracaoRepository;

    public Optional<Integracao> getIntegracaoById(Integer id) {
        return integracaoRepository.findById(id);
    }

    public LocalDateTime verificarAccessToken(Integer integracaoId) {
        Integracao integracao = getIntegracaoById(integracaoId).get();
        LocalDateTime expiraEm = integracao.getAccess_token_expire_date();

        return expiraEm;
    }
}
