package com.vulpix.api.service;

import com.vulpix.api.dto.dashboard.DashKpiDto;
import com.vulpix.api.dto.dashboard.PostInsightsDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.PostInsights;
import com.vulpix.api.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {
    @Autowired
    DashboardRepository dashboardRepository;

    public List<PostInsights> buscaMetricaUltimoPost(Empresa empresa) {
        return dashboardRepository.findLatestInsightsForEachPostByEmpresa(empresa.getId());
    }

    public List<PostInsightsDto> buscaMetricasPorDia(Empresa empresaId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = dashboardRepository.findLatestPostInsightsByEmpresaAndDate(empresaId.getId(), startDate, endDate);

        String[] diasDaSemana = {"Seg", "Ter", "Quar", "Qui", "Sex", "Sab", "Dom"};

        List<PostInsightsDto> insightsDTOs = new ArrayList<>();
        for (Object[] result : results) {
            int weekDay = ((BigDecimal) result[1]).intValue();

            if (weekDay < 1 || weekDay > 7) {
                System.err.println("Valor inesperado de weekDay: " + weekDay);
                continue;
            }

            if (weekDay == 7) {
                weekDay = 0;
            } else {
                weekDay -= 1;
            }

            PostInsightsDto dto = PostInsightsDto.builder()
                    .day(((Timestamp) result[0]).toLocalDateTime().toLocalDate())
                    .name(diasDaSemana[weekDay])
                    .Likes(convertToLong(result[2]))
                    .Views(convertToLong(result[3]))
                    .Shares(convertToLong(result[4]))
                    .Saves(convertToLong(result[5]))
                    .Comments(convertToLong(result[6]))
                    .build();
            insightsDTOs.add(dto);
        }

        return insightsDTOs;
    }

    private Long convertToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        return 0L;
    }

    public DashKpiDto buscaKpisPorPeriodo(UUID empresaId) {
        List<Object[]> metricasList = dashboardRepository.findMetricasBasicas(empresaId);

        Integer totalImpressoes = 0;
        Integer totalShares = 0;
        Integer totalSaves = 0;

        if (!metricasList.isEmpty() && metricasList.get(0) != null) {
            Object[] metricas = metricasList.get(0);
            totalImpressoes = metricas[0] != null ? ((Number) metricas[0]).intValue() : 0;
            totalShares = metricas[1] != null ? ((Number) metricas[1]).intValue() : 0;
            totalSaves = metricas[2] != null ? ((Number) metricas[2]).intValue() : 0;
        }

        BigDecimal taxaCompartilhamento = totalImpressoes > 0 
            ? new BigDecimal(totalShares).multiply(new BigDecimal("100")).divide(new BigDecimal(totalImpressoes), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
            
        BigDecimal taxaSaves = totalImpressoes > 0 
            ? new BigDecimal(totalSaves).multiply(new BigDecimal("100")).divide(new BigDecimal(totalImpressoes), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        Integer alcanceUltimoPost = dashboardRepository.findAlcanceUltimoPost(empresaId);
        if (alcanceUltimoPost == null) {
            alcanceUltimoPost = 0;
        }
        
        return new DashKpiDto(
            taxaCompartilhamento,
            taxaSaves,
            totalImpressoes,
            alcanceUltimoPost
        );
    }
}