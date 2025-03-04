package com.vulpix.api.controller;

import com.vulpix.api.dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.dto.Criativo.CriativoResponseDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.integracoes.agentai.CriativosService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/criativos")
@Tag(name = "Controller de Criativos", description = "Gerenciamento dos criativos associados às empresas.")
public class CriativoController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Autowired
    private CriativosService criativosService;

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
                                            name = "Exemplo de resposta",
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
                    @ApiResponse(responseCode = "204", description = "Nenhum criativo encontrado."),
                    @ApiResponse(responseCode = "404", description = "Empresa não encontrada ou dados inválidos.")
            }
    )
    @GetMapping
    public ResponseEntity<Page<CriativoResponseDto>> buscarCriativos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Page<CriativoResponseDto> criativos = criativosService.buscaCriativosGerados(empresa, page, size, dataInicio, dataFim);

        if (criativos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(criativos);
    }

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
                                            name = "Exemplo de resposta",
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
                    @ApiResponse(responseCode = "404", description = "Criativo ou empresa não encontrado.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CriativoRequisicaoDto> buscaCriativoPorId(@PathVariable UUID id) {
        CriativoRequisicaoDto response = criativosService.buscaPorId(id);
        return ResponseEntity.status(200).body(response);
    }
}