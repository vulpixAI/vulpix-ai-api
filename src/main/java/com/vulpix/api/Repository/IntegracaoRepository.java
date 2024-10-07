package com.vulpix.api.Repository;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IntegracaoRepository extends JpaRepository<Integracao, UUID> {
    Optional<Integracao> findByEmpresaAndTipo(Empresa empresa, TipoIntegracao tipo);
    Optional<Integracao> findByEmpresaId(UUID idEmpresa);

    @Query("SELECT i FROM Integracao i WHERE i.empresa.id = :idEmpresa and i.tipo = 'INSTAGRAM'")
    Optional<Integracao> findIntegracaoByEmpresaId(UUID idEmpresa);
}
