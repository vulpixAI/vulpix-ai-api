package com.vulpix.api.repository;

import com.vulpix.api.entity.PostInsights;
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

    @Query(value = """
        SELECT
            SUM(pi.impressions) AS total_visualizacoes_perfil
        FROM
            post_insights pi
        JOIN
            publicacao p ON pi.fk_publicacao = p.id_publicacao
        WHERE
            pi.created_at = (
                SELECT MAX(created_at)
                FROM post_insights
                WHERE fk_publicacao = p.id_publicacao
            )
        AND
            p.fk_empresa = :empresa_id
        """, nativeQuery = true)
    Integer findImpressoesTotais(
            @Param("empresa_id") UUID empresaId
    );

    @Query(value = """
        SELECT
            pi.impressions AS alcance_ultima_postagem
        FROM
            post_insights pi
        JOIN
            publicacao p ON pi.fk_publicacao = p.id_publicacao
        WHERE
            p.id_publicacao = (
                SELECT p2.id_publicacao
                FROM publicacao p2
                WHERE p2.fk_empresa = :empresa_id
                ORDER BY p2.created_at DESC
                LIMIT 1
            )
            AND pi.created_at = (
                SELECT MAX(created_at)
                FROM post_insights
                WHERE fk_publicacao = p.id_publicacao
            )
            AND p.fk_empresa = :empresa_id
        """, nativeQuery = true)
    Integer findAlcanceTotalUltimoPost(
            @Param("empresa_id") UUID empresaId
    );

    @Query(value = """
            SELECT
                p.fk_empresa,
                ROUND(SUM(COALESCE(pi.shares, 0)) * 100.0 / NULLIF(SUM(pi.impressions), 0), 2) AS taxa_compartilhamento,
                ROUND(SUM(COALESCE(pi.saves, 0)) * 100.0 / NULLIF(SUM(pi.impressions), 0), 2) AS taxa_salvamento
            FROM
                post_insights pi
            JOIN
                publicacao p ON pi.fk_publicacao = p.id_publicacao
            WHERE
                p.fk_empresa = :empresa_id
                AND pi.created_at = (
                    SELECT MAX(created_at)
                    FROM post_insights
                    WHERE fk_publicacao = p.id_publicacao
                )
            GROUP BY
                p.fk_empresa
                """, nativeQuery = true)
    List<Object[]> findTaxas(@Param("empresa_id") UUID empresaId);

    @Query(value = """
            SELECT 
                COALESCE(SUM(pi.impressions), 0) as total_impressions,
                COALESCE(SUM(pi.shares), 0) as total_shares,
                COALESCE(SUM(pi.saves), 0) as total_saves
            FROM 
                post_insights pi
            JOIN 
                publicacao p ON pi.fk_publicacao = p.id_publicacao
            WHERE 
                p.fk_empresa = :empresa_id
            GROUP BY
                p.fk_empresa
            """, nativeQuery = true)
    List<Object[]> findMetricasBasicas(@Param("empresa_id") UUID empresaId);

    @Query(value = """
            SELECT 
                COALESCE(pi.impressions, 0) as alcance_ultimo_post
            FROM 
                post_insights pi
            JOIN 
                publicacao p ON pi.fk_publicacao = p.id_publicacao
            WHERE 
                p.fk_empresa = :empresa_id
            ORDER BY 
                p.created_at DESC
            LIMIT 1
            """, nativeQuery = true)
    Integer findAlcanceUltimoPost(@Param("empresa_id") UUID empresaId);
}
