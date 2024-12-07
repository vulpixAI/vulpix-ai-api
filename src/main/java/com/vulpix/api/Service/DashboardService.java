package com.vulpix.api.Service;

import com.vulpix.api.Dto.Dashboard.PostInsightsDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.PostInsights;
import com.vulpix.api.Repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    DashboardRepository dashboardRepository;

    public List<PostInsights> buscaMetricaUltimoPost(Empresa empresa) {
        List<PostInsights> metricasPorPost = dashboardRepository.findLatestInsightsForEachPostByEmpresa(empresa.getId());

        if (metricasPorPost.isEmpty()) return null;
        return metricasPorPost;
    }

    public List<PostInsightsDto> buscaMetricasPorDia(Empresa empresaId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = dashboardRepository.findLatestPostInsightsByEmpresaAndDate(empresaId.getId(), startDate, endDate);

        List<PostInsightsDto> insightsDTOs = new ArrayList<>();
        for (Object[] result : results) {
            PostInsightsDto dto = PostInsightsDto.builder()
                    .day(((java.sql.Timestamp) result[0]).toLocalDateTime().toLocalDate())
                    .weekDay(((BigDecimal) result[1]).intValue())
                    .totalLikes(result[2] instanceof Integer ? ((Integer) result[2]).longValue() : ((BigDecimal) result[2]).longValue())
                    .totalViews(result[3] instanceof Integer ? ((Integer) result[3]).longValue() : ((BigDecimal) result[3]).longValue())
                    .totalShares(result[4] instanceof Integer ? ((Integer) result[4]).longValue() : ((BigDecimal) result[4]).longValue())
                    .totalSaves(result[5] instanceof Integer ? ((Integer) result[5]).longValue() : ((BigDecimal) result[5]).longValue())
                    .totalComments(result[6] instanceof Integer ? ((Integer) result[6]).longValue() : ((BigDecimal) result[6]).longValue())  
                    .build();
            insightsDTOs.add(dto);
        }
        return insightsDTOs;
    }
}
