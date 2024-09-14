package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.dto.PostPublicacaoDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import com.vulpix.api.services.PublicacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<Publicacao>> buscarPosts(@PathVariable UUID empresaId) {
        return publicacaoService.buscarPosts(empresaId);
    }

    @GetMapping("/ordenado/{empresaId}")
    public ResponseEntity<List<Publicacao>> postsOrdenado(@PathVariable UUID empresaId) {
        ResponseEntity<List<Publicacao>> responseEntity = buscarPosts(empresaId);
        List<Publicacao> posts = responseEntity.getBody();

        if (posts != null && !posts.isEmpty()) {
            for (int i = 1; i < posts.size(); i++) {
                Publicacao x = posts.get(i);
                int j = i - 1;
                while (j >= 0 && posts.get(j).getLikeCount() < x.getLikeCount()) {
                    posts.set(j + 1, posts.get(j));
                    j--;
                }
                posts.set(j + 1, x);
            }
        }else{
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Publicacao> criarPost(@RequestBody PostPublicacaoDto post){

        Optional<Empresa> empresa = empresaRepository.findById(post.getFkEmpresa());

        if (empresa.isEmpty()){
            return ResponseEntity.status(404).build();
        }

        Publicacao novoPost = new Publicacao();
        novoPost.setLegenda(post.getCaption());
        novoPost.setUrlMidia(post.getImageUrl());
        novoPost.setEmpresa(empresa.get());
        novoPost.setCreated_at(LocalDateTime.now());

        OffsetDateTime dataAgendamento = post.getAgendamento();
        if (dataAgendamento != null) {
            novoPost.setDataPublicacao(dataAgendamento);
        }

        Optional<Integracao> integracao = empresa.get().getIntegracoes().stream()
                .filter(i -> TipoIntegracao.INSTAGRAM.equals(i.getTipo()))
                .findFirst();


        if (integracao.isEmpty()){
            return ResponseEntity.status(404).build();
        }

        if (dataAgendamento != null) {
            LocalDateTime dataAgendamentoLocal = dataAgendamento.toLocalDateTime();
            Duration delay = Duration.between(LocalDateTime.now(), dataAgendamentoLocal);
            if (!delay.isNegative()) {
                agendamentoCriarPost(integracao.get(), novoPost, delay);
                return ResponseEntity.status(201).body(novoPost);
            }
        }

        Long containerId = publicacaoService.criarContainer(integracao.get(), novoPost);
        publicacaoService.criarPublicacao(integracao.get(), containerId);
        return ResponseEntity.status(201).body(publicacaoRepository.save(novoPost));
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

