package com.vulpix.api.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Integer> {
}