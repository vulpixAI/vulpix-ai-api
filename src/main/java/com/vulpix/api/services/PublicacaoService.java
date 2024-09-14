package com.vulpix.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vulpix.api.dto.GetPublicacaoDto;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.integracao.Graph;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.util.UriComponentsBuilder;

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
    private PublicacaoRepository publicacaoRepository;

    @Autowired
    public PublicacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long criarContainer(Integracao integracao, Publicacao post) {
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("URL da Imagem: " + post.getUrlMidia());
        System.out.println("Legenda: " + post.getLegenda());
        System.out.println("Token de Acesso: " + integracao.getAccess_token());
        System.out.println("Ig Id user: " + integracao.getIgUserId());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image_url", post.getUrlMidia());
        body.add("caption", post.getLegenda());
        body.add("access_token", integracao.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());

                String idString = root.path("id").asText();

                return Long.parseLong(idString);
            } else {
                throw new RuntimeException("Falha ao criar o container: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Erro ao criar o container: " + e.getResponseBodyAsString());
            throw e;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String criarPublicacao(Integracao integracao, Long idContainer) {
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media_publish";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("creation_id", idContainer)
                .queryParam("access_token", integracao.getAccess_token());

        String finalUrl = uriBuilder.toUriString();

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(finalUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Falha ao criar a publicação: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro ao chamar a API: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<List<Publicacao>> buscarPosts(UUID idEmpresa) {
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

            List<GetPublicacaoDto> posts = objectMapper.convertValue(dataNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Publicacao.class));

            List<Publicacao> resposta = posts.stream().map(item -> {
                Publicacao postDto = new Publicacao();
                postDto.setIdReturned(item.getId());
                postDto.setLegenda(item.getLegenda());
                postDto.setTipoMidia(item.getTipoMidia());
                postDto.setUrlMidia(item.getUrlMidia());
                postDto.setDataPublicacao(item.getDataPublicacao());
                postDto.setLikeCount(item.getLikeCount());
                return postDto;
            }).collect(Collectors.toList());

            salvarPostNoBanco(resposta);

            return ResponseEntity.ok(resposta);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    public void salvarPostNoBanco(List<Publicacao> posts){
        List<Publicacao> postsSalvar = new ArrayList<>();
        for (Publicacao post : posts) {
            Optional<Publicacao> postExistente = publicacaoRepository.findByIdInsta(post.getIdReturned());
            if (postExistente.isEmpty()) {
                postsSalvar.add(post);
            }
        }
        if (!postsSalvar.isEmpty()) {
            publicacaoRepository.saveAll(postsSalvar);
        }
    }

    

}
