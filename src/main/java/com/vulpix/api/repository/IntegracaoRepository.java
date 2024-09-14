package com.vulpix.api.repository;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IntegracaoRepository extends JpaRepository<Integracao, UUID> {
    Optional<Integracao> findByEmpresaAndTipo(Empresa empresa, TipoIntegracao tipo);
    Optional<Integracao> findByEmpresaId(UUID idEmpresa);
}
