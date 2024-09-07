package com.vulpix.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vulpix.api.entity.Empresa;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByRazaoSocialAndCnpj(String razaoSocial, String cnpj);
}
