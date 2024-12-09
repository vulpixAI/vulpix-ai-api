package com.vulpix.api.Service;

import com.vulpix.api.Utils.Enum.TipoIntegracao;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Repository.IntegracaoRepository;
import com.vulpix.api.Service.Integracoes.Graph.TokenService;
import com.vulpix.api.Service.Usuario.UsuarioService;
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
    @Autowired
    private UsuarioService usuarioService;

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

        Integracao integracaoRenovada = tokenService.renovarAccessToken(integracao);
        return integracaoRepository.save(integracaoRenovada);
    }

    public Integracao retornaIntegracao(Empresa empresa) {
        Optional<Integracao> integracaoExistente = integracaoRepository.findIntegracaoByEmpresaId(empresa.getId());

        return integracaoExistente.orElse(null);
    }

}
