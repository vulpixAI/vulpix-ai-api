package com.vulpix.api.repository;

import com.vulpix.api.entity.PostInsights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsightRepository  extends JpaRepository<PostInsights, UUID> {

}
