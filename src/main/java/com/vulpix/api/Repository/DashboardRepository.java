package com.vulpix.api.Repository;

import com.vulpix.api.Entity.PostInsights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<PostInsights, UUID> {
    @Query(value = """
    SELECT pi.* 
    FROM post_insights pi 
    INNER JOIN (
        SELECT fk_publicacao, MAX(created_at) AS max_created_at
        FROM post_insights
        WHERE fk_publicacao IN (
            SELECT id_publicacao FROM publicacao WHERE fk_empresa = :empresaId
        )
        GROUP BY fk_publicacao
    ) latest_insights 
    ON pi.fk_publicacao = latest_insights.fk_publicacao 
    AND pi.created_at = latest_insights.max_created_at
""", nativeQuery = true)
    List<PostInsights> findLatestInsightsForEachPostByEmpresa(@Param("empresaId") UUID empresaId);

}
