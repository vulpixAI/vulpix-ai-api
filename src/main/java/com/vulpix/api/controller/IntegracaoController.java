package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.UsuarioRepository;
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
    private IntegracaoRepository integracaoRepository;

    @PostMapping
    public ResponseEntity<Integracao> habilitar(
            @RequestBody Integracao novaIntegracao
    ) {
        Optional<Integracao> integracaoAtiva = integracaoRepository.findByEmpresaAndTipo(novaIntegracao.getEmpresa(), novaIntegracao.getTipo());
        if (integracaoAtiva.isPresent()) {
            return ResponseEntity.status(409).build();
        }
        novaIntegracao.setId(null);
        return ResponseEntity.status(201).body(integracaoRepository.save(novaIntegracao));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Integracao> atualizar(
            @PathVariable UUID id,
            @RequestBody Integracao integracaoAtualizada
    ) {

        Optional<Integracao> integracaoExistente = integracaoRepository.findById(id);

        if (integracaoExistente.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Integracao integracao = integracaoExistente.get();

        if (integracaoAtualizada.getAccess_token() != null) {
            integracao.setAccess_token(integracaoAtualizada.getAccess_token());
        }
        if (integracaoAtualizada.getClient_id() != null) {
            integracao.setClient_id(integracaoAtualizada.getClient_id());
        }
        if (integracaoAtualizada.getClient_secret() != null) {
            integracao.setClient_secret(integracaoAtualizada.getClient_secret());
        }
        if (integracaoAtualizada.getIgUserId() != null) {
            integracao.setIgUserId(integracaoAtualizada.getIgUserId());
        }

        return ResponseEntity.status(200).body(integracaoRepository.save(integracao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        Optional<Integracao> integracaoExistente = integracaoRepository.findById(id);

        if (integracaoExistente.isEmpty()){
            return ResponseEntity.status(404).build();
        }

        integracaoRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }
}
