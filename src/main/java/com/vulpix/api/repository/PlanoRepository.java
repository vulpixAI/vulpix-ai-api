package com.vulpix.api.repository;

import com.vulpix.api.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Integer> {
}