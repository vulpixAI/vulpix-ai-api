package com.vulpix.api.controller;

import com.vulpix.api.dto.criativo.CriativoRequisicaoDto;
import com.vulpix.api.dto.criativo.CriativoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("/criativos")
@Tag(name = "Criativo", description = "Gerenciamento dos criativos associados às empresas.")
public interface CriativoController {
    @Operation(
            summary = "Busca criativos gerados",
            description = "Retorna uma página de criativos gerados pela empresa autenticada, com opções de filtros por data.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Criativos encontrados com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "content": [
                                                                {
                                                                    "id": "1d0b8f2d-3c57-48f6-8c16-a3b4f20ff7b6",
                                                                    "nome": "Criativo Exemplo",
                                                                    "dataCriacao": "2024-11-24T15:30:00"
                                                                }
                                                            ],
                                                            "pageable": {
                                                                "pageNumber": 0,
                                                                "pageSize": 10
                                                            },
                                                            "totalElements": 1,
                                                            "totalPages": 1
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Nenhum criativo encontrado.", content = @Content(examples = @ExampleObject())),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<Page<CriativoResponseDto>> buscarCriativos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    );

    @Operation(
            summary = "Busca criativo por ID",
            description = "Retorna as informações de um criativo específico pelo seu ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Criativo encontrado com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "id": "1d0b8f2d-3c57-48f6-8c16-a3b4f20ff7b6",
                                                            "nome": "Criativo Exemplo",
                                                            "descricao": "Descrição detalhada do criativo.",
                                                            "dataCriacao": "2024-11-24T15:30:00"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Criativo não encontrado.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Criativo não encontrado.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<CriativoRequisicaoDto> buscaCriativoPorId(@PathVariable UUID id);
}