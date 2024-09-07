package com.vulpix.api.repository;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicacaoRepository extends JpaRepository<Publicacao, Integer> {

}
