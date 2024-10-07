package com.vulpix.api.Controller;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;

    @PatchMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable UUID id, @RequestBody Empresa empresaAtualizada) {
        Empresa empresaAtualizadaSalva = empresaService.atualizarEmpresa(id, empresaAtualizada);
        if (empresaAtualizadaSalva == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(empresaAtualizadaSalva);
    }

}