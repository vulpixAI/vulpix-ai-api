package com.vulpix.api.service;

import com.vulpix.api.dto.Dashboard.DashKpiDto;
import com.vulpix.api.dto.Dashboard.PostInsightsDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.PostInsights;
import com.vulpix.api.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public DashKpiDto buscaKpisPorPeriodo(Empresa empresa) {
        UUID empresaId = empresa.getId();

        List<Object[]> results = dashboardRepository.findTaxas(empresaId);

        BigDecimal taxaSalvo = null;
        BigDecimal taxaShares = null;

        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            taxaShares = (BigDecimal) row[1];
            taxaSalvo = (BigDecimal) row[2];
        }

        return DashKpiDto.builder()
                .taxaSaves(taxaSalvo)
                .taxaCompartilhamento(taxaShares)
                .visualizacoesTotais(dashboardRepository.findImpressoesTotais(empresaId))
                .alcanceUltimoPost(dashboardRepository.findAlcanceTotalUltimoPost(empresaId))
                .build();
    }
}