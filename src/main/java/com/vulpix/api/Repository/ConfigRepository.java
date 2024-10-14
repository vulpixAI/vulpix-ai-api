package com.vulpix.api.Repository;

import com.vulpix.api.Entity.ConfigPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfigRepository extends JpaRepository<ConfigPrompt, UUID> {
    Optional<ConfigPrompt> findByEmpresaId(UUID idEmpresa);
}
