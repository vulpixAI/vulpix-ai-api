package com.vulpix.api.Service;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.PostInsights;
import com.vulpix.api.Repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
