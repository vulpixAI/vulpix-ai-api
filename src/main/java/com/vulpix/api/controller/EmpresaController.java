package com.vulpix.api.controller;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<Empresa> cadastrar(@RequestBody Empresa novaEmpresa) {
        if (empresaService.empresaExistePorRazaoSocialECnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj())) {
            return ResponseEntity.status(409).build();
        }
        Empresa empresaSalva = empresaService.salvarEmpresa(novaEmpresa);
        return ResponseEntity.status(201).body(empresaSalva);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable UUID id, @RequestBody Empresa empresaAtualizada) {
        Empresa empresaAtualizadaSalva = empresaService.atualizarEmpresa(id, empresaAtualizada);
        if (empresaAtualizadaSalva == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(empresaAtualizadaSalva);
    }

}