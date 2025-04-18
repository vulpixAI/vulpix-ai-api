package com.vulpix.api.controller;

import com.vulpix.api.dto.dashboard.DashKpiDto;
import com.vulpix.api.dto.dashboard.PostInsightsDto;
import com.vulpix.api.entity.PostInsights;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/dash")
@Tag(name = "Dashboard", description = "Endpoints para consultas de dados do dashboard.")
public interface DashboardController {
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
                    @ApiResponse(responseCode = "204", description = "Nenhuma métrica encontrada.", content = @Content(examples = @ExampleObject())),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping("/grafico-ultima-metrica-post")
    ResponseEntity<List<PostInsights>> buscaUltimaMetricaPost();

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
                    @ApiResponse(responseCode = "204", description = "Nenhuma métrica encontrada no período.", content = @Content(examples = @ExampleObject())),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping("/grafico-metricas-por-dia")
    ResponseEntity<List<PostInsightsDto>> buscaMetricasPorDia(@RequestParam LocalDate data_inicio, @RequestParam LocalDate data_fim);

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
                    @ApiResponse(responseCode = "204", description = "Nenhum KPI encontrado.", content = @Content(examples = @ExampleObject())),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping("/kpis")
    ResponseEntity<DashKpiDto> buscaDadosKpi();
}