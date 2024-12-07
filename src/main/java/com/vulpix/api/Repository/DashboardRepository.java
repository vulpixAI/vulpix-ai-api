package com.vulpix.api.Repository;

import com.vulpix.api.Entity.PostInsights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query(value = """
            WITH LastMetrics AS (
                SELECT
                    DATE_TRUNC('day', created_at) AS day,
                    EXTRACT(DOW FROM created_at) AS week_day,
                    likes,
                    impressions,
                    shares,
                    saves,
                    comments,
                    ROW_NUMBER() OVER (PARTITION BY DATE_TRUNC('day', created_at) ORDER BY created_at DESC) AS rn
                FROM
                    post_insights
            )
            SELECT
                current.day,
                current.week_day,
                current.likes - COALESCE(prev.likes, 0) AS total_likes,
                current.impressions - COALESCE(prev.impressions, 0) AS total_views,
                current.shares - COALESCE(prev.shares, 0) AS total_shares,
                current.saves - COALESCE(prev.saves, 0) AS total_saves,
                current.comments - COALESCE(prev.comments, 0) AS total_comments
            FROM
                LastMetrics current
            LEFT JOIN
                LastMetrics prev ON current.day = prev.day + INTERVAL '1 day' AND prev.rn = 1
            WHERE
                current.rn = 1
            ORDER BY
                current.day;
        """, nativeQuery = true)
    List<Object[]> findLatestPostInsightsByEmpresaAndDate(
            @Param("empresa_id") UUID empresaId,
            @Param("start_date") LocalDate startDate,
            @Param("end_date") LocalDate endDate
    );
}
