package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.dto.Integracao.Req.IntegracaoDto;
import com.vulpix.api.dto.Integracao.Req.IntegracaoMapper;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.UsuarioRepository;
import com.vulpix.api.services.EmpresaService;
import com.vulpix.api.services.IntegracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integracoes")
public class IntegracaoController {

    @Autowired
    private IntegracaoService integracaoService;

    @PostMapping
    public ResponseEntity<Integracao> habilitar(@RequestBody IntegracaoDto novaIntegracao) {
        if (novaIntegracao == null) return null;
        Empresa empresa = integracaoService.identificaEmpresa(novaIntegracao.getIdEmpresa());

        Integracao integracao = IntegracaoMapper.criaEntidadeIntegracao(novaIntegracao, empresa);

        Optional<Integracao> integracaoAtiva = integracaoService.findByEmpresaAndTipo(integracao.getEmpresa(), integracao.getTipo());
        if (integracaoAtiva.isPresent()) return ResponseEntity.status(409).build();

        return ResponseEntity.status(201).body(integracaoService.save(integracao));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Integracao> atualizar(@PathVariable UUID id, @RequestBody Integracao integracaoAtualizada) {
        Optional<Integracao> integracaoExistente = integracaoService.getIntegracaoById(id);
        if (integracaoExistente.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Integracao integracao = integracaoExistente.get();
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

        return ResponseEntity.status(200).body(integracaoService.save(integracao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Optional<Integracao> integracaoExistente = integracaoService.getIntegracaoById(id);
        if (integracaoExistente.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        integracaoService.deleteById(id);
        return ResponseEntity.status(204).build();
    }

}
