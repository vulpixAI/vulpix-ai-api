package com.vulpix.api.services;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntegracaoService {
    @Autowired
    private IntegracaoRepository integracaoRepository;

    public Optional<Integracao> getIntegracaoById(UUID id) {
        return integracaoRepository.findById(id);
    }

    public Optional<LocalDateTime> verificarAccessToken(UUID integracaoId) {
        return getIntegracaoById(integracaoId)
                .map(Integracao::getAccessTokenExpireDate);
    }

    public Optional<Integracao> findByEmpresaAndTipo(Empresa empresa, TipoIntegracao tipo) {
        return integracaoRepository.findByEmpresaAndTipo(empresa, tipo);
    }

    public Integracao save(Integracao integracao) {
        return integracaoRepository.save(integracao);
    }

    public void deleteById(UUID id) {
        if (!integracaoRepository.existsById(id)) {
            throw new RuntimeException("Integração não encontrada");
        }
        integracaoRepository.deleteById(id);
    }
}
