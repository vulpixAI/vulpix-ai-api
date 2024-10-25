package com.vulpix.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vulpix.api.Entity.Empresa;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    Optional<Empresa> findByRazaoSocialAndCnpj(String razaoSocial, String cnpj);

    Optional<Empresa> findByUsuarioId(UUID usuarioId);

    Optional<Empresa> findByUsuarioEmail(String email);

}
