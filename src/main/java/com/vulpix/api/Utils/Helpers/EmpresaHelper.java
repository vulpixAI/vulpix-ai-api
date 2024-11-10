package com.vulpix.api.Utils.Helpers;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpresaHelper {
    @Autowired
    EmpresaRepository empresaRepository;

    public Empresa buscarEmpresaPeloUsuario(String email) {
        Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioEmail(email);

        if (empresaOpt.isPresent()) {
            return empresaOpt.get();
        }

        throw new EntityNotFoundException("Empresa não encontrada para o usuário autenticado");
    }
}
