package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Criativo;
import com.vulpix.api.Entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CriativoRepository extends JpaRepository<Criativo, UUID> {

}
