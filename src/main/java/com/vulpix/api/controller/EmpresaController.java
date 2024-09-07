package com.vulpix.api.controller;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaRepository EmpresaRepository;

    @PostMapping()
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
}
