package com.vulpix.api.repository;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntegracaoRepository extends JpaRepository<Integracao, Integer> {
    Optional<Integracao> findByEmpresaAndTipo(Empresa empresa, TipoIntegracao tipo);
}
