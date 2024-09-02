package com.vulpix.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vulpix.api.dto.PublicacaoApiExternaDto;
import com.vulpix.api.dto.PublicacaoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PublicacaoController {
    @GetMapping()
    public ResponseEntity<List<PublicacaoDto>> buscarPosts() {

        String igUserId = "17841468739640580";
        String accessToken = "";
        String fields = "id,caption,media_type,media_url,timestamp,like_count";

        String url = "https://graph.facebook.com/v17.0/" + igUserId + "/media?fields=" + fields + "&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        String rawResponse = restTemplate.getForObject(url, String.class);

        if (rawResponse == null) {
            return ResponseEntity.noContent().build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode dataNode = rootNode.path("data");

            if (!dataNode.isArray()) {
                return ResponseEntity.noContent().build();
            }

            List<PublicacaoApiExternaDto> posts = objectMapper.convertValue(dataNode, objectMapper.getTypeFactory().constructCollectionType(List.class, PublicacaoApiExternaDto.class));
            List<PublicacaoDto> resposta = posts.stream().map(item -> {
                PublicacaoDto postDto = new PublicacaoDto();
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
    public ResponseEntity<List<PublicacaoDto>> postsOrdenado() {
        ResponseEntity<List<PublicacaoDto>> responseEntity = buscarPosts();
        List<PublicacaoDto> posts = responseEntity.getBody();

        if (posts != null && !posts.isEmpty()) {
            for (int i = 1; i < posts.size(); i++) {
                PublicacaoDto x = posts.get(i);
                int j = i - 1;
                while (j >= 0 && posts.get(j).getDataPublicacao().isAfter(x.getDataPublicacao())) {
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

