package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndSenha(String email, String senha);
    Optional<Usuario> findByEmpresa(Empresa empresa);
    Boolean existsByEmail(String email);
}
