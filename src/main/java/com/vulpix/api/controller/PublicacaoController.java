package com.vulpix.api.controller;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.dto.GetPublicacaoDto;
import com.vulpix.api.dto.PostPublicacaoDto;
import com.vulpix.api.dto.PostPublicacaoResponse;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import com.vulpix.api.services.PublicacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/posts")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;
    @Autowired
    private PublicacaoRepository publicacaoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }
    @GetMapping("/{empresaId}")
    public ResponseEntity<List<GetPublicacaoDto>> buscarPosts(@PathVariable UUID empresaId) {
        return publicacaoService.buscarPosts(empresaId);
    }

    @GetMapping("/ordenado/{empresaId}")
    public ResponseEntity<List<GetPublicacaoDto>> postsOrdenado(@PathVariable UUID empresaId) {
        ResponseEntity<List<GetPublicacaoDto>> responseEntity = buscarPosts(empresaId);
        List<GetPublicacaoDto> posts = responseEntity.getBody();

        if (posts != null && !posts.isEmpty()) {
            quickSort(posts, 0, posts.size() - 1);
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.status(204).build();
        }
    }

    private void quickSort(List<GetPublicacaoDto> posts, int indMenor, int indMaior) {
        if (indMenor < indMaior) {
            int pi = particiona(posts, indMenor, indMaior);
            quickSort(posts, indMenor, pi - 1);
            quickSort(posts, pi + 1, indMaior);
        }
    }

    private int particiona(List<GetPublicacaoDto> posts, int indMenor, int indMaior) {
        GetPublicacaoDto pivot = posts.get(indMaior);
        int i = indMenor - 1;

        for (int j = indMenor; j < indMaior; j++) {
            if (posts.get(j).getLikeCount() > pivot.getLikeCount()) {
                i++;
                Collections.swap(posts, i, j);
            }
        }
        Collections.swap(posts, i + 1, indMaior);
        return i + 1;
    }

    @GetMapping("/somaLikes/{empresaId}")
    public ResponseEntity<Integer> somarLikes(@PathVariable UUID empresaId) {
        ResponseEntity<List<GetPublicacaoDto>> responseEntity = buscarPosts(empresaId);
        List<GetPublicacaoDto> posts = responseEntity.getBody();

        if (posts != null && !posts.isEmpty()) {
            int somaTotalLikes = somarLikePosts(posts, 0);
            return ResponseEntity.ok(somaTotalLikes);
        } else {
            return ResponseEntity.status(204).build();
        }
    }

    private int somarLikePosts(List<GetPublicacaoDto> posts, int indice) {
        if (indice == posts.size()) {
            return 0;
        }
        return posts.get(indice).getLikeCount() + somarLikePosts(posts, indice + 1);
    }

    @PostMapping
    public ResponseEntity<PostPublicacaoResponse> criarPost(@RequestBody PostPublicacaoDto post) {
        Optional<Empresa> empresa = empresaRepository.findById(post.getFkEmpresa());

        if (empresa.isEmpty()){
            return ResponseEntity.status(404).build();
        }

        Publicacao novoPost = new Publicacao();
        novoPost.setLegenda(post.getCaption());
        novoPost.setUrlMidia(post.getImageUrl());
        novoPost.setEmpresa(empresa.get());
        novoPost.setCreated_at(LocalDateTime.now());
        novoPost.setIdReturned(post.getIdReturned());

        OffsetDateTime dataAgendamento = post.getAgendamento();
        if (dataAgendamento != null) {
            novoPost.setDataPublicacao(dataAgendamento);
        }

        Optional<Integracao> integracao = empresa.get().getIntegracoes().stream()
                .filter(i -> TipoIntegracao.INSTAGRAM.equals(i.getTipo()))
                .findFirst();

        if (integracao.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        if (dataAgendamento != null) {
            Duration delay = Duration.between(LocalDateTime.now(), dataAgendamento);
            if (!delay.isNegative()) {
                agendamentoCriarPost(integracao.get(), novoPost, delay);
                return ResponseEntity.status(201).body(createResponseDto(novoPost));
            }
        }

        Long containerId = publicacaoService.criarContainer(integracao.get(), novoPost);
        String postIdReturned = publicacaoService.criarPublicacao(integracao.get(), containerId);
        novoPost.setIdReturned(postIdReturned);
        Publicacao savedPost = publicacaoRepository.save(novoPost);
        return ResponseEntity.status(201).body(createResponseDto(savedPost));
    }

    private PostPublicacaoResponse createResponseDto(Publicacao post) {
        PostPublicacaoResponse responseDto = new PostPublicacaoResponse();
        responseDto.setLegenda(post.getLegenda());
        responseDto.setId(post.getId());
        responseDto.setFkEmpresa(post.getEmpresa().getId());
        return responseDto;
    }
    private void agendamentoCriarPost(Integracao integracao, Publicacao post, Duration delay) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(() -> {
            Long containerId = publicacaoService.criarContainer(integracao, post);
            publicacaoService.criarPublicacao(integracao, containerId);
            publicacaoRepository.save(post);
        }, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}

