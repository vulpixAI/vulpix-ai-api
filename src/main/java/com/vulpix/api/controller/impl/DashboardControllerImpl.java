package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.DashboardController;
import com.vulpix.api.dto.Dashboard.DashKpiDto;
import com.vulpix.api.dto.Dashboard.PostInsightsDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.PostInsights;
import com.vulpix.api.service.DashboardService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DashboardControllerImpl implements DashboardController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Autowired
    private DashboardService dashboardService;

    @Override
    public ResponseEntity<List<PostInsights>> buscaUltimaMetricaPost() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        List<PostInsights> response = dashboardService.buscaMetricaUltimoPost(empresa);

        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<List<PostInsightsDto>> buscaMetricasPorDia(@RequestParam LocalDate data_inicio, @RequestParam LocalDate data_fim) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        List<PostInsightsDto> response = dashboardService.buscaMetricasPorDia(empresa, data_inicio, data_fim);

        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<DashKpiDto> buscaDadosKpi() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        DashKpiDto response = dashboardService.buscaKpisPorPeriodo(empresa);

        if (response == null) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(response);
    }
}