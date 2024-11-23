package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Publicacao.Insights.PublicacaoInsightDto;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Enum.StatusPublicacao;
import com.vulpix.api.Utils.Enum.TipoIntegracao;
import com.vulpix.api.Dto.Publicacao.GetPublicacaoDto;
import com.vulpix.api.Dto.Publicacao.PostPublicacaoDto;
import com.vulpix.api.Dto.Publicacao.PostPublicacaoResponse;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Entity.Publicacao;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Repository.PublicacaoRepository;
import com.vulpix.api.Service.Integracoes.Graph.PublicacaoService;
import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/posts")
@Tag(name = "Controller de Publicação")
public class PublicacaoController {
    @Autowired
    private PublicacaoService publicacaoService;
    @Autowired
    private PublicacaoRepository publicacaoRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;

    @Operation(summary = "Criar um novo post",
            description = "Cria um novo post para a empresa informada. O post deve incluir a legenda e a URL da mídia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"legenda\":\"Novo Post\",\"id\":\"1\",\"fkEmpresa\":\"empresa-1\"}"),
                                    @ExampleObject(value = "{\"legenda\":\"Post de Verão\",\"id\":\"2\",\"fkEmpresa\":\"empresa-1\"}"),
                                    @ExampleObject(value = "{\"legenda\":\"Post de Natal\",\"id\":\"3\",\"fkEmpresa\":\"empresa-1\"}"),
                                    @ExampleObject(value = "{\"legenda\":\"Post de Aniversário\",\"id\":\"4\",\"fkEmpresa\":\"empresa-1\"}"),
                                    @ExampleObject(value = "{\"legenda\":\"Post de Lançamento\",\"id\":\"5\",\"fkEmpresa\":\"empresa-1\"}"),
                                    @ExampleObject(value = "{\"legenda\":\"Post de Black Friday\",\"id\":\"6\",\"fkEmpresa\":\"empresa-1\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PostMapping
    public ResponseEntity<PostPublicacaoResponse> criarPost(@RequestBody PostPublicacaoDto post) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        Publicacao novoPost = new Publicacao();
        novoPost.setLegenda(post.getCaption());
        novoPost.setUrlMidia(post.getImageUrl());
        novoPost.setEmpresa(empresa);
        novoPost.setCreated_at(LocalDateTime.now());

        OffsetDateTime dataAgendamento = post.getAgendamento();

        if (dataAgendamento != null && dataAgendamento.isAfter(OffsetDateTime.now())) {
            novoPost.setDataPublicacao(dataAgendamento);
            novoPost.setStatus(StatusPublicacao.AGENDADA);
            Publicacao savedPost = publicacaoRepository.save(novoPost);
            return ResponseEntity.status(201).body(createResponseDto(savedPost));
        }

        Integracao integracao = empresa.getIntegracoes().stream()
                .filter(i -> TipoIntegracao.INSTAGRAM.equals(i.getTipo()))
                .findFirst()
                .orElse(null);

        if (integracao == null) {
            return ResponseEntity.status(404).build();
        }

        Long containerId = publicacaoService.criarContainer(integracao, novoPost);
        String postIdReturned = publicacaoService.criarPublicacao(integracao, containerId);
        novoPost.setIdReturned(postIdReturned);
        novoPost.setStatus(StatusPublicacao.PUBLICADA);

        Publicacao postSalvo = publicacaoRepository.save(novoPost);
        return ResponseEntity.status(201).body(createResponseDto(postSalvo));
    }

    private PostPublicacaoResponse createResponseDto(Publicacao post) {
        PostPublicacaoResponse responseDto = new PostPublicacaoResponse();
        responseDto.setLegenda(post.getLegenda());
        responseDto.setId(post.getId());
        responseDto.setFkEmpresa(post.getEmpresa().getId());
        return responseDto;
    }

    @Operation(summary = "Gera uma publicação criativa com a AI",
            description = "Gera uma publicação baseada na solicitação do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Publicação gerada com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(value = "[{\"id\":\"1\",\"legenda\":\"Post 1\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post1\",\"dataPublicacao\":\"2024-11-01T10:00:00Z\",\"likeCount\":10}]"),
                                            @ExampleObject(value = "[{\"id\":\"2\",\"legenda\":\"Post 2\",\"tipoMidia\":\"video\",\"urlMidia\":\"http://exemplo.com/post2\",\"dataPublicacao\":\"2024-11-02T10:00:00Z\",\"likeCount\":20}]"),
                                            @ExampleObject(value = "[{\"id\":\"3\",\"legenda\":\"Post 3\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post3\",\"dataPublicacao\":\"2024-11-03T10:00:00Z\",\"likeCount\":30}]"),
                                            @ExampleObject(value = "[{\"id\":\"4\",\"legenda\":\"Post 4\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post4\",\"dataPublicacao\":\"2024-11-04T10:00:00Z\",\"likeCount\":40}]"),
                                            @ExampleObject(value = "[{\"id\":\"5\",\"legenda\":\"Post 5\",\"tipoMidia\":\"video\",\"urlMidia\":\"http://exemplo.com/post5\",\"dataPublicacao\":\"2024-11-05T10:00:00Z\",\"likeCount\":50}]"),
                                            @ExampleObject(value = "[{\"id\":\"6\",\"legenda\":\"Post 6\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post6\",\"dataPublicacao\":\"2024-11-06T10:00:00Z\",\"likeCount\":60}]")
                                    })),
                    @ApiResponse(responseCode = "502", description = "Erro ao gerar a publicação.")
            })
    @PostMapping("/gerar-post")
    public ResponseEntity<PublicacaoGeradaRetorno> gerarPublicacao(@RequestBody String userRequest) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        PublicacaoGeradaRetorno retorno = empresaService.buscaCriativos(empresa, userRequest);

        if (retorno.getImagem1() == null) return ResponseEntity.status(502).build();

        return ResponseEntity.status(201).body(retorno);
    }

    @PostMapping("/gerar-legenda")
    @Operation(summary = "Gerar legenda para a publicação",
            description = "Gera uma legenda com base na solicitação do usuário e na empresa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Legenda gerada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"legenda\": \"Esta é a legenda gerada.\" }"))),
            @ApiResponse(responseCode = "502", description = "Erro ao buscar a legenda, empresa ou solicitação inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro ao buscar a legenda.\" }"))),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Solicitação inválida.\" }")))
    })
    public ResponseEntity<Map<String, String>> gerarLegenda(@RequestBody String userRequest) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        String legenda = empresaService.buscaLegenda(empresa, userRequest);

        if (legenda == null) return ResponseEntity.status(502).build();

        Map<String, String> response = new HashMap<>();
        response.put("legenda", legenda);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Buscar posts por empresa",
            description = "Retorna uma lista de publicações associadas a uma empresa especificada pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de publicações retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "[{\"id\":\"1\",\"legenda\":\"Post 1\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post1\",\"dataPublicacao\":\"2024-11-01T10:00:00Z\",\"likeCount\":10}]"),
                                    @ExampleObject(value = "[{\"id\":\"2\",\"legenda\":\"Post 2\",\"tipoMidia\":\"video\",\"urlMidia\":\"http://exemplo.com/post2\",\"dataPublicacao\":\"2024-11-02T10:00:00Z\",\"likeCount\":20}]"),
                                    @ExampleObject(value = "[{\"id\":\"3\",\"legenda\":\"Post 3\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post3\",\"dataPublicacao\":\"2024-11-03T10:00:00Z\",\"likeCount\":30}]"),
                                    @ExampleObject(value = "[{\"id\":\"4\",\"legenda\":\"Post 4\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post4\",\"dataPublicacao\":\"2024-11-04T10:00:00Z\",\"likeCount\":40}]"),
                                    @ExampleObject(value = "[{\"id\":\"5\",\"legenda\":\"Post 5\",\"tipoMidia\":\"video\",\"urlMidia\":\"http://exemplo.com/post5\",\"dataPublicacao\":\"2024-11-05T10:00:00Z\",\"likeCount\":50}]"),
                                    @ExampleObject(value = "[{\"id\":\"6\",\"legenda\":\"Post 6\",\"tipoMidia\":\"image\",\"urlMidia\":\"http://exemplo.com/post6\",\"dataPublicacao\":\"2024-11-06T10:00:00Z\",\"likeCount\":60}]")
                            })),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @GetMapping()
    public ResponseEntity<Page<GetPublicacaoDto>> buscarPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Page<GetPublicacaoDto> posts = publicacaoService.buscarPosts(empresa.getId(), page, size, dataInicio, dataFim);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Somar likes das publicações utilizando Recursão",
            description = "Retorna a soma total de likes de todas as publicações da empresa especificada pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Soma total de likes retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "150"), // Exemplo de soma total
                                    @ExampleObject(value = "200"), // Outro exemplo
                                    @ExampleObject(value = "300"), // E mais exemplos
                                    @ExampleObject(value = "50"),
                                    @ExampleObject(value = "75"),
                                    @ExampleObject(value = "100")
                            })),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para somar os likes.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Nenhuma publicação encontrada.\" }")))
    })
    @GetMapping("/somar-likes-publicacao")
    public ResponseEntity<Integer> somarLikes() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);


        List<GetPublicacaoDto> posts = publicacaoService.buscarPostsSemPaginacao(empresa.getId());

        if (posts != null && !posts.isEmpty()) {
            int somaLikes = somarLikesRecursivo(posts, 0);
            return ResponseEntity.status(200).body(somaLikes);
        }

        return ResponseEntity.status(204).build();
    }

    private int somarLikesRecursivo(List<GetPublicacaoDto> posts, int index) {
        if (index >= posts.size()) {
            return 0;
        }
        return posts.get(index).getLikeCount() + somarLikesRecursivo(posts, index + 1);
    }

    @Operation(summary = "Buscar publicações por data",
            description = "Retorna a publicação da empresa especificada pelo ID e pela data de publicação informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicação encontrada com sucesso.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para a data especificada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Nenhuma publicação encontrada.\" }"))),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida. O formato da data pode ser incorreto.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Formato de data inválido.\" }")))
    })
    @GetMapping("/buscar-por-data")
    public ResponseEntity<GetPublicacaoDto> buscarPorData(
            @RequestParam String dataPublicacao) {
        try {
            OffsetDateTime dataBusca = OffsetDateTime.parse(dataPublicacao + "T00:00:00Z");
            UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
            String emailUsuario = userDetails.getUsername();
            Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);


            List<GetPublicacaoDto> posts = publicacaoService.buscarPostsSemPaginacao(empresa.getId());

            if (posts == null || posts.isEmpty()) {
                return ResponseEntity.status(204).build();
            }

            posts.sort(Comparator.comparing(GetPublicacaoDto::getDataPublicacao));
            return posts.stream()
                    .filter(post -> post.getDataPublicacao().toLocalDate().isEqual(dataBusca.toLocalDate()))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).build());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Exportar publicações para CSV",
            description = "Gera um arquivo CSV contendo todas as publicações da empresa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo CSV gerado e retornado com sucesso.",
                    content = @Content(mediaType = "text/csv",
                            examples = {
                                    @ExampleObject(value = "ID,Legenda,Tipo Mídia,URL Mídia,Data Publicação,Likes\n" +
                                            "1,Exemplo de Legenda,imagem,https://exemplo.com/imagem.jpg,2024-11-03,150\n" +
                                            "2,Outra Legenda,video,https://exemplo.com/video.mp4,2024-11-02,200")
                            })),
            @ApiResponse(responseCode = "204", description = "Nenhuma publicação encontrada para exportar.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Nenhuma publicação encontrada.\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro ao exportar os dados.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro ao exportar os dados.\" }")))
    })
    @GetMapping("/exportar-csv")
    public ResponseEntity<InputStreamResource> exportarPublicacoesCSV() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);
        List<GetPublicacaoDto> posts = publicacaoService.buscarPostsSemPaginacao(empresa.getId());

        if (posts == null || posts.isEmpty()) return ResponseEntity.status(204).build();
      
        String arquivo = "publicacao.csv";
        try (OutputStream file = new FileOutputStream(arquivo);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(file))) {
            writer.write("ID,Legenda,Tipo Mídia,URL Mídia,Data Publicação,Likes\n");
            writer.newLine();
            for (GetPublicacaoDto post : posts) {
                writer.write(String.format("%s,%s,%s,%s,%s,%d\n",
                        post.getId(),
                        post.getLegenda(),
                        post.getTipoMidia(),
                        post.getUrlMidia(),
                        post.getDataPublicacao() != null ? post.getDataPublicacao().toString() : "",
                        post.getLikeCount() != null ? post.getLikeCount() : 0));
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(arquivo));
            return ResponseEntity.status(200)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + arquivo)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (IOException e) {
            System.err.println("Erro ao exportar os dados: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacaoInsightDto> buscaInsightPorId(@PathVariable String id) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        PublicacaoInsightDto response = publicacaoService.buscaInsightPost(id, empresa.getId());
        if (response == null) return ResponseEntity.status(404).build();

        return ResponseEntity.status(200).body(response);
    }
}