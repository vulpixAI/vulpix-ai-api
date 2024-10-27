package com.vulpix.api.Repository;

import com.vulpix.api.Entity.ConfigPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConfigRepository extends JpaRepository<ConfigPrompt, UUID> {
    Optional<ConfigPrompt> findByEmpresaId(UUID idEmpresa);
    @Query(value = "SELECT form FROM config_prompt WHERE fk_empresa = :empresaId", nativeQuery = true)
    String findFormAsStringByEmpresaId(@Param("empresaId") UUID empresaId);
}
