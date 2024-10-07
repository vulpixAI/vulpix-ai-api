package com.vulpix.api.controller;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.dto.Publicacao.GetPublicacaoDto;
import com.vulpix.api.dto.Publicacao.PostPublicacaoDto;
import com.vulpix.api.dto.Publicacao.PostPublicacaoResponse;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import com.vulpix.api.services.Integracoes.Graph.PublicacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PublicacaoController {
    private PublicacaoService publicacaoService;
    @Autowired
    private PublicacaoRepository publicacaoRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping("/{empresaId}")
    public ResponseEntity<List<GetPublicacaoDto>> buscarPosts(@PathVariable UUID empresaId) {
        return publicacaoService.buscarPosts(empresaId);
    }

    @PostMapping
    public ResponseEntity<PostPublicacaoResponse> criarPost(@RequestBody PostPublicacaoDto post) {
        Optional<Empresa> empresa = empresaRepository.findById(post.getFkEmpresa());

        if (empresa.isEmpty()) {
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
            Duration delay = Duration.between(LocalDateTime.now(), dataAgendamento.toLocalDateTime());
            if (!delay.isNegative()) {
                return ResponseEntity.status(201).body(createResponseDto(novoPost));
            }
        }

        Integracao integracao = empresa.get().getIntegracoes().stream()
                .filter(i -> TipoIntegracao.INSTAGRAM.equals(i.getTipo()))
                .findFirst()
                .orElse(null);

        if (integracao == null) {
            return ResponseEntity.status(404).build();
        }
        Long containerId = publicacaoService.criarContainer(integracao, novoPost);
        String postIdReturned = publicacaoService.criarPublicacao(integracao, containerId);
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
}

