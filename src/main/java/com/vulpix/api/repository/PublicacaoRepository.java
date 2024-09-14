package com.vulpix.api.repository;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    Optional<Publicacao> findByIdInsta(Long idInsta);
}
