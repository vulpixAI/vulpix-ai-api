package com.vulpix.api.Controller;

import com.vulpix.api.Entity.ConfigPrompt;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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

    @PostMapping("/form/{idEmpresa}")
    public ResponseEntity<String> cadastrarFormulario(@PathVariable UUID idEmpresa, @RequestBody FormularioRequisicaoDto formulario) {
        Empresa empresa = empresaService.buscaPorId(idEmpresa);
        if (empresa == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada.");

        empresaService.cadastrarFormulario(empresa, formulario);
        return ResponseEntity.ok("Formulário cadastrado com sucesso.");
    }

    @GetMapping("/form/{idEmpresa}")
    public void buscaFormulario(@PathVariable UUID idEmpresa){
        Empresa empresa = empresaService.buscaPorId(idEmpresa);

        empresaService.buscaFormulario(empresa);

//        if (configPrompt != null) {
//            return ResponseEntity.ok(configPrompt);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
    }
}