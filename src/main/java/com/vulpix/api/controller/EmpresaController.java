package com.vulpix.api.controller;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaRepository EmpresaRepository;

    @PostMapping
    public ResponseEntity<Empresa> cadastrar(
            @RequestBody Empresa novaEmpresa
    ) {
        Optional<Empresa> empresaExistente = EmpresaRepository.findByRazaoSocialAndCnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj());

        if (empresaExistente.isPresent()){
            return ResponseEntity.status(409).build();
        }
        novaEmpresa.setId(null);
        return ResponseEntity.status(201).body(EmpresaRepository.save(novaEmpresa));
    }

    @PatchMapping
    public ResponseEntity<Empresa> atualizar(
            @PathVariable UUID id,
            @RequestBody Empresa empresaAtualizada
    ){
        Optional<Empresa> empresa = EmpresaRepository.findById(id);

        if (empresa.isEmpty()){
            return ResponseEntity.status(404).build();
        }
        Empresa empresaExistente = empresa.get();

        if (empresaAtualizada.getEmail() != null && !empresaAtualizada.getEmail().isEmpty()) {
            empresaExistente.setEmail(empresaAtualizada.getEmail());
        }

        if (empresaAtualizada.getTelefone() != null && !empresaAtualizada.getTelefone().isEmpty()) {
            empresaExistente.setTelefone(empresaAtualizada.getTelefone());
        }

        EmpresaRepository.save(empresaExistente);
        return ResponseEntity.status(200).body(empresaExistente);
    }
}
