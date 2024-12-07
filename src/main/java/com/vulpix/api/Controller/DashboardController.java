package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.Dto.Dashboard.DashKpiDto;
import com.vulpix.api.Dto.Dashboard.PostInsightsDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.PostInsights;
import com.vulpix.api.Service.DashboardService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dash")
public class DashboardController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/grafico-ultima-metrica-post")
    public ResponseEntity<List<PostInsights>> buscaUltimaMetricaPost() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        List<PostInsights> response = dashboardService.buscaMetricaUltimoPost(empresa);
        if (response == null) return ResponseEntity.status(204).build();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/grafico-metricas-por-dia")
    public ResponseEntity<List<PostInsightsDto>> buscaMetricasPorDia(@RequestParam LocalDate data_inicio, @RequestParam LocalDate data_fim) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        List<PostInsightsDto> response = dashboardService.buscaMetricasPorDia(empresa, data_inicio, data_fim);
        if (response == null) return ResponseEntity.status(204).build();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/kpís")
    public ResponseEntity<DashKpiDto> buscaDadosKpi() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        DashKpiDto response = dashboardService.buscaKpisPorPeriodo(empresa);
        if (response == null) return ResponseEntity.status(204).build();
        return ResponseEntity.status(200).body(response);
    }
}
