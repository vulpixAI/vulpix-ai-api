package com.vulpix.api.repository;

import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.utils.enums.StatusPublicacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    Optional<Publicacao> findByIdReturned(String idReturned);
    void deleteByEmpresaId(UUID empresaId);

    List<Publicacao> findByStatus(StatusPublicacao status);

    Page<Publicacao> findByEmpresaId(UUID idEmpresa, Pageable pageable);
    List<Publicacao> findByEmpresaId(UUID idEmpresa);

    Page<Publicacao> findByEmpresaIdAndDataPublicacaoBetween(UUID idEmpresa, OffsetDateTime dataFiltroInicio, OffsetDateTime dataFiltroFim, Pageable pageable);
}