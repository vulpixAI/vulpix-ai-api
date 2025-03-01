package com.vulpix.api.utils.helpers;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaHelper {
    @Autowired
    EmpresaRepository empresaRepository;

    public Empresa buscarEmpresaPeloUsuario(String email) {
        return empresaRepository.findByUsuarioEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada para o usuário autenticado."));
    }

    public Boolean isCnpjCadastrado(String cnpj) {
        return empresaRepository.existsByCnpj(cnpj);
    }
}
