package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    Optional<Publicacao> findByIdReturned(String idReturned);
}
