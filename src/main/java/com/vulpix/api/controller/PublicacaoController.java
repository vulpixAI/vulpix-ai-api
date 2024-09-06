package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.entity.Publicacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PublicacaoController {
    @GetMapping()
    public ResponseEntity<List<Publicacao>> buscarPosts() {

        String igUserId = "17841468739640580";
        String accessToken = "";
        String fields = "id,caption,media_type,media_url,timestamp,like_count";

        String url = "https://graph.facebook.com/v17.0/" + igUserId + "/media?fields=" + fields + "&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        String rawResponse = restTemplate.getForObject(url, String.class);

        if (rawResponse == null) {
            return ResponseEntity.status(204).build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode dataNode = rootNode.path("data");

            if (!dataNode.isArray()) {
                return ResponseEntity.noContent().build();
            }

            List<Publicacao> posts = objectMapper.convertValue(dataNode, objectMapper.getTypeFactory().constructCollectionType(List.class, Publicacao.class));
            List<Publicacao> resposta = posts.stream().map(item -> {
                Publicacao postDto = new Publicacao();
                postDto.setId(item.getId());
                postDto.setLegenda(item.getLegenda());
                postDto.setTipoMidia(item.getTipoMidia());
                postDto.setUrlMidia(item.getUrlMidia());
                postDto.setDataPublicacao(item.getDataPublicacao());
                postDto.setLikeCount(item.getLikeCount());
                return postDto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(resposta);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

    }

    @GetMapping("/ordenado")
    public ResponseEntity<List<Publicacao>> postsOrdenado() {
        ResponseEntity<List<Publicacao>> responseEntity = buscarPosts();
        List<Publicacao> posts = responseEntity.getBody();

        if (posts != null && !posts.isEmpty()) {
            for (int i = 1; i < posts.size(); i++) {
                Publicacao x = posts.get(i);
                int j = i - 1;
                while (j >= 0 && posts.get(j).getLikeCount() > x.getLikeCount()) {
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

