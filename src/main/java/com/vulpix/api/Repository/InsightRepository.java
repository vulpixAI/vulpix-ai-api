package com.vulpix.api.Repository;

import com.vulpix.api.Entity.Criativo;
import com.vulpix.api.Entity.PostInsights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsightRepository  extends JpaRepository<PostInsights, UUID> {

}
