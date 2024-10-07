package com.vulpix.api.services;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.services.Integracoes.Graph.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntegracaoService {
    @Autowired
    private IntegracaoRepository integracaoRepository;
    @Autowired
    EmpresaRepository empresaRepository;
    @Autowired
    private TokenService tokenService;

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

    public Empresa identificaEmpresa(UUID idEmpresa) {
        Optional<Empresa> empresa = empresaRepository.findById(idEmpresa);
        if(empresa.isPresent()) return empresa.get();

        return null;
    }

    public Integracao atualizaIntegracao(UUID id, Integracao integracaoAtualizada) {
        Optional<Integracao> integracaoExiste = integracaoRepository.findById(id);

        if (integracaoExiste.isEmpty()) return null;

        Integracao integracao = integracaoExiste.get();

        if (integracaoAtualizada.getAccessToken() != null) {
            integracao.setAccessToken(integracaoAtualizada.getAccessToken());
        }
        if (integracaoAtualizada.getClientId() != null) {
            integracao.setClientId(integracaoAtualizada.getClientId());
        }
        if (integracaoAtualizada.getClientSecret() != null) {
            integracao.setClientSecret(integracaoAtualizada.getClientSecret());
        }
        if (integracaoAtualizada.getIgUserId() != null) {
            integracao.setIgUserId(integracaoAtualizada.getIgUserId());
        }

        Integracao integracaoRenovado = tokenService.renovarAccessToken(integracao);

        return integracaoRenovado;
    }


}
