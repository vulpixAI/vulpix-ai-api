package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Publicacao;
import com.vulpix.api.Utils.Enum.StatusPublicacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
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

    Page<Publicacao> findByEmpresaIdAndDataPublicacaoAfter(UUID idEmpresa, OffsetDateTime dataFiltro, Pageable pageable);
}