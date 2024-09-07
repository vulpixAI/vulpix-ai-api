package com.vulpix.api.services;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.integracao.Graph;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class PublicacaoService {

    private final RestTemplate restTemplate;
    @Autowired
    private IntegracaoRepository integracaoRepository;

    @Autowired
    public PublicacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String criarContainer(Graph integracao, String mediaUrl, String caption) {
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image_url", mediaUrl);
        body.add("caption", caption);
        body.add("access_token", integracao.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Falha ao criar o container: " + response.getStatusCode());
        }
    }

    public ResponseEntity<List<Publicacao>> buscarPosts(Integer idEmpresa) {
        Optional<Integracao> integracaoOpt = integracaoRepository.findByEmpresaId(idEmpresa);

        if (integracaoOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Integracao integracao = integracaoOpt.get();

        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media?fields=" + Graph.FIELDS + "&access_token=" + integracao.getAccess_token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> rawResponseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        String rawResponse = rawResponseEntity.getBody();

        if (rawResponse == null || rawResponse.isEmpty()) {
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

            List<Publicacao> posts = objectMapper.convertValue(dataNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Publicacao.class));

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

}
