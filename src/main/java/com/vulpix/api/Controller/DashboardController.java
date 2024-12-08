package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Dashboard.DashKpiDto;
import com.vulpix.api.Dto.Dashboard.PostInsightsDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.PostInsights;
import com.vulpix.api.Service.DashboardService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dash")
@Tag(name = "Dashboard", description = "Endpoints para consultas de dados do dashboard.")
public class DashboardController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;
    @Autowired
    private DashboardService dashboardService;

    @Operation(
            summary = "Busca a última métrica de um post",
            description = "Retorna as métricas do último post publicado pela empresa do usuário autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Métricas encontradas com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Exemplo de resposta",
                                            value = """
                                                        [
                                                            {
                                                                "id": "f7d10ae3-c9e5-46e8-9a20-d53420ff3b6a",
                                                                "metrica": "Visualizações",
                                                                "valor": 1200,
                                                                "dataCriacao": "2024-11-01T15:30:00"
                                                            }
                                                        ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Nenhuma métrica encontrada."),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
            }
    )
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

    @Operation(
            summary = "Busca métricas de posts por período",
            description = "Retorna as métricas de posts para a empresa do usuário autenticado, filtradas por um intervalo de datas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Métricas encontradas com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Exemplo de resposta",
                                            value = """
                                                        [
                                                            {
                                                                "data": "2024-11-01",
                                                                "visualizacoes": 1000,
                                                                "curtidas": 150,
                                                                "comentarios": 20
                                                            },
                                                            {
                                                                "data": "2024-11-02",
                                                                "visualizacoes": 800,
                                                                "curtidas": 120,
                                                                "comentarios": 15
                                                            }
                                                        ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Nenhuma métrica encontrada no período."),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
            }
    )

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

    @Operation(
            summary = "Busca dados de KPIs",
            description = "Retorna os principais indicadores de desempenho (KPIs) para a empresa do usuário autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "KPIs encontrados com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Exemplo de resposta",
                                            value = """
                                                        {
                                                            "totalPosts": 50,
                                                            "engajamentoMedio": 75.5,
                                                            "crescimentoSeguidores": 120
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Nenhum KPI encontrado."),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
            }
    )
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
