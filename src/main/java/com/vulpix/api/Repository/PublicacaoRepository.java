package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    Optional<Publicacao> findByIdReturned(String idReturned);
    void deleteByEmpresaId(UUID empresaId);

}