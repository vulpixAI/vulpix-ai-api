package com.vulpix.api.controller;

import com.vulpix.api.dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.dto.Publicacao.GetPublicacaoDto;
import com.vulpix.api.dto.Publicacao.Insights.PublicacaoInsightDto;
import com.vulpix.api.dto.Publicacao.PostPublicacaoDto;
import com.vulpix.api.dto.Publicacao.PostPublicacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/posts")
@Tag(name = "Publicação")
public interface PublicacaoController {
    @Operation(summary = "Criar um novo post",
            description = "Cria um novo post para a empresa informada. O post deve incluir a legenda e a URL da mídia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{ \"legenda\":\"Novo Post\",\"id\":\"1\",\"fkEmpresa\":\"empresa-1\" }")
                            })),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PostMapping
    ResponseEntity<PostPublicacaoResponse> criarPost(@RequestBody PostPublicacaoDto post);

    @Operation(summary = "Gera uma publicação criativa com a AI",
            description = "Gera uma publicação baseada na solicitação do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Publicação gerada com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(value = """
                                                    [
                                                        { "id":"1","legenda":"Post 1","tipoMidia":"image","urlMidia":"http://exemplo.com/post1","dataPublicacao":"2024-11-01T10:00:00Z","likeCount":10 },
                                                        { "id":"2","legenda":"Post 2","tipoMidia":"image","urlMidia":"http://exemplo.com/post2","dataPublicacao":"2024-11-01T10:00:00Z","likeCount":20 }
                                                    ]
                                                    """),
                                    })),
                    @ApiResponse(responseCode = "500", description = "Erro ao gerar a publicação.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 500, \"detail\": \"Erro ao gerar publicação.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            })
    @PostMapping("/gerar-post")
    ResponseEntity<PublicacaoGeradaRetorno> gerarPublicacao(@RequestBody String userRequest);

    @PostMapping("/gerar-legenda")
    @Operation(summary = "Gerar legenda para a publicação",
            description = "Gera uma legenda com base na solicitação do usuário e na empresa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Legenda gerada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"legenda\": \"Esta é a legenda gerada.\" }")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 400, \"detail\": \"Requisição inválida.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar legenda.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 500, \"detail\": \"Erro ao gerar legenda.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    ResponseEntity<Map<String, String>> gerarLegenda(@RequestBody String userRequest);

    @Operation(summary = "Buscar posts por empresa",
            description = "Retorna uma lista de publicações associadas a uma empresa especificada pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de publicações retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = """
                                            [
                                                { "id":"1","legenda":"Post 1","tipoMidia":"image","urlMidia":"http://exemplo.com/post1","dataPublicacao":"2024-11-01T10:00:00Z","likeCount":10 },
                                                { "id":"2","legenda":"Post 2","tipoMidia":"image","urlMidia":"http://exemplo.com/post2","dataPublicacao":"2024-11-01T10:00:00Z","likeCount":20 }
                                            ]
                                            """),
                            })),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping()
    ResponseEntity<Page<GetPublicacaoDto>> buscarPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    );

    @Operation(summary = "Somar likes das publicações utilizando Recursão",
            description = "Retorna a soma total de likes de todas as publicações da empresa especificada pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Soma total de likes retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{ \"likes\": 150 }")
                            })),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para somar os likes.", content = @Content(examples = @ExampleObject()))
    })
    @GetMapping("/somar-likes-publicacao")
    ResponseEntity<Integer> somarLikes();

    @Operation(summary = "Buscar publicações por data",
            description = "Retorna a publicação da empresa especificada pelo ID e pela data de publicação informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicação encontrada com sucesso.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para a data especificada.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 400, \"detail\": \"Requisição inválida. O formato da data pode ser incorreto.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping("/buscar-por-data")
    ResponseEntity<GetPublicacaoDto> buscarPorData(@RequestParam String dataPublicacao);

    @Operation(summary = "Exportar publicações para CSV",
            description = "Gera um arquivo CSV contendo todas as publicações da empresa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo CSV gerado e retornado com sucesso.",
                    content = @Content(mediaType = "text/csv",
                            examples = {
                                    @ExampleObject(value = """
                                            ID,Legenda,Tipo Mídia,URL Mídia,Data Publicação,Likes
                                            1,Exemplo de Legenda,imagem,https://exemplo.com/imagem.jpg,2024-11-03,150
                                            2,Outra Legenda,video,https://exemplo.com/video.mp4,2024-11-02,200
                                            """)
                            })),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para exportar.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(responseCode = "500", description = "Erro ao exportar os dados.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 500, \"detail\": \"Erro ao exportar os dados.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping("/exportar-csv")
    ResponseEntity<InputStreamResource> exportarPublicacoesCSV();

    @Operation(
            summary = "Busca um insight por ID",
            description = "Retorna as informações detalhadas de um insight específico associado à empresa autenticada.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Insight encontrado com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "id": "123456",
                                                            "titulo": "Título do Insight",
                                                            "descricao": "Descrição detalhada do insight.",
                                                            "dataCriacao": "2024-11-01T10:00:00",
                                                            "empresaId": "e6a8b7c9-4d2f-4f1a-9cde-123456789abc"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Insight não encontrado.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Insight não encontrado.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<PublicacaoInsightDto> buscaInsightPorId(@PathVariable String id);
}