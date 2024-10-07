package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Integracao.Resquest.IntegracaoDto;
import com.vulpix.api.Dto.Integracao.Resquest.IntegracaoMapper;
import com.vulpix.api.Dto.Integracao.Resquest.IntegracaoUpdateDto;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.IntegracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<Integracao> atualizar(@PathVariable UUID id, @RequestBody IntegracaoUpdateDto integracaoAtualizada) {
        Optional<Integracao> integracaoExistente = integracaoService.getIntegracaoById(id);
        if (integracaoExistente.isEmpty()) return ResponseEntity.status(404).build();

        Empresa empresa = integracaoService.identificaEmpresa(integracaoExistente.get().getEmpresa().getId());

        Integracao integracao = IntegracaoMapper.criaEntidadeAtualizada(empresa, integracaoAtualizada);

        integracaoService.atualizaIntegracao(id, integracao);

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
