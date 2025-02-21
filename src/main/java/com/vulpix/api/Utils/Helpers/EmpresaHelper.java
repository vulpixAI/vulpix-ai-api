package com.vulpix.api.Utils.Helpers;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class EmpresaHelper {
    @Autowired
    EmpresaRepository empresaRepository;

    public Empresa buscarEmpresaPeloUsuario(String email) {
        return empresaRepository.findByUsuarioEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada para o usuário autenticado."));
    }
}
