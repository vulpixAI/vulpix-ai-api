package com.vulpix.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vulpix.api.entity.Empresa;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    Optional<Empresa> findByRazaoSocialAndCnpj(String razaoSocial, String cnpj);
}
