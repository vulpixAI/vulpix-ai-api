package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.services.PublicacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }
    @GetMapping("/{empresaId}")
    public ResponseEntity<List<Publicacao>> buscarPosts(@PathVariable Integer empresaId) {
        return publicacaoService.buscarPosts(empresaId);
    }

    @GetMapping("/ordenado/{empresaId}")
    public ResponseEntity<List<Publicacao>> postsOrdenado(@PathVariable Integer empresaId) {
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
}

