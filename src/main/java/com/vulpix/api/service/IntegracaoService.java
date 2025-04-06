package com.vulpix.api.service;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.exception.exceptions.ConflitoException;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.service.integracoes.graph.TokenService;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.enums.TipoIntegracao;
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

    public Optional<Integracao> buscaIntegracaoPorId(UUID id) {
        return integracaoRepository.findById(id);
    }

    public Optional<LocalDateTime> verificarAccessToken(UUID integracaoId) {
        return buscaIntegracaoPorId(integracaoId)
                .map(Integracao::getAccessTokenExpireDate);
    }

    public Integracao buscaIntegracaoPorTipo(Empresa empresa, TipoIntegracao tipo) {
        return integracaoRepository.findByEmpresaAndTipo(empresa, tipo).orElseThrow(() -> new NaoEncontradoException("Integração não encontrada."));
    }

    public boolean verificaExistenciaIntegracaoPorTipo(Empresa empresa, TipoIntegracao tipo) {
        return integracaoRepository.existsByEmpresaAndTipo(empresa, tipo);
    }

    public Integracao cadastrarIntegracao(Integracao integracao, Empresa empresa) {
        if (verificaExistenciaIntegracaoPorTipo(empresa, integracao.getTipo())) {
            throw new ConflitoException("Já existe uma integração ativa.");
        }

        return integracaoRepository.save(integracao);
    }

    public void excluirIntegracao(UUID id) {
        if (!integracaoRepository.existsById(id)) {
            throw new NaoEncontradoException("Integração não encontrada.");
        }
        integracaoRepository.deleteById(id);
    }

    public Integracao atualizaIntegracao(UUID id, Integracao integracaoAtualizada) {
        Integracao integracao = integracaoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Integração não encontrada."));

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
        return integracaoRepository.findIntegracaoByEmpresaId(empresa.getId()).orElseThrow(() -> new NaoEncontradoException("Integração não encontrada."));
    }
}