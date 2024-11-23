package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Criativo;
import com.vulpix.api.Entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CriativoRepository extends JpaRepository<Criativo, UUID> {
    Page<Criativo> findAllByEmpresaOrderByCreatedAtDesc(Empresa empresa, Pageable pageable);
}
