package com.vulpix.api.repository;

import com.vulpix.api.entity.Criativo;
import com.vulpix.api.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CriativoRepository extends JpaRepository<Criativo, UUID> {

    Page<Criativo> findAllByEmpresaOrderByCreatedAtDesc(Empresa empresa, Pageable pageable);

    Page<Criativo> findAllByEmpresaAndCreatedAtBetweenOrderByCreatedAtDesc(Empresa empresa, LocalDateTime dataFiltroInicio, LocalDateTime dataFiltroFim, Pageable pageable);

}
