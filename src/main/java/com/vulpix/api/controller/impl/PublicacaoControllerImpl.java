package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.PublicacaoController;
import com.vulpix.api.dto.agent.PublicacaoGeradaRetorno;
import com.vulpix.api.dto.publicacao.GetPublicacaoDto;
import com.vulpix.api.dto.publicacao.Insights.PublicacaoInsightDto;
import com.vulpix.api.dto.publicacao.PostPublicacaoDto;
import com.vulpix.api.dto.publicacao.PostPublicacaoResponse;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.integracoes.graph.PublicacaoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.enums.StatusPublicacao;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
public class PublicacaoControllerImpl implements PublicacaoController {
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

    @Override
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
        novoPost.setDataPublicacao(OffsetDateTime.now());
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

    @Override
    public ResponseEntity<PublicacaoGeradaRetorno> gerarPublicacao(@RequestBody String userRequest) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        PublicacaoGeradaRetorno retorno = empresaService.buscaCriativos(empresa, userRequest);

        if (retorno.getImagem1() == null) return ResponseEntity.status(502).build();

        return ResponseEntity.status(201).body(retorno);
    }

    @Override
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

    @Override
    public ResponseEntity<Page<GetPublicacaoDto>> buscarPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Page<GetPublicacaoDto> posts = publicacaoService.buscarPosts(empresa.getId(), page, size, dataInicio, dataFim);
        if (posts.isEmpty()) return ResponseEntity.status(204).build();
        return ResponseEntity.ok(posts);
    }

    @Override
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

    @Override
    public ResponseEntity<GetPublicacaoDto> buscarPorData(@RequestParam String dataPublicacao) {
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

    @Override
    public ResponseEntity<InputStreamResource> exportarPublicacoesCSV() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        List<GetPublicacaoDto> posts = publicacaoService.buscarPostsSemPaginacao(empresa.getId());


        if (posts == null || posts.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        Queue<GetPublicacaoDto> filaPosts = new LinkedList<>(posts);

        Stack<String> pilhaLinhas = new Stack<>();
        pilhaLinhas.push("ID,Legenda,Tipo Mídia,URL Mídia,Data Publicação,Likes\n");

        while (!filaPosts.isEmpty()) {
            GetPublicacaoDto post = filaPosts.poll();
            String linha = String.format("%s,%s,%s,%s,%s,%d\n",
                    post.getId(),
                    post.getLegenda(),
                    post.getTipoMidia(),
                    post.getUrlMidia(),
                    post.getDataPublicacao() != null ? post.getDataPublicacao().toString() : "",
                    post.getLikeCount() != null ? post.getLikeCount() : 0);
            pilhaLinhas.push(linha);
        }

        String arquivo = "publicacao.csv";
        try (OutputStream file = new FileOutputStream(arquivo);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(file))) {

            while (!pilhaLinhas.isEmpty()) {
                writer.write(pilhaLinhas.pop());
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

    @Override
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