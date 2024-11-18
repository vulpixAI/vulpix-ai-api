package com.vulpix.api.Service.Integracoes.Graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.vulpix.api.Dto.Publicacao.GetPublicacaoDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Entity.Publicacao;
import com.vulpix.api.Utils.Enum.StatusPublicacao;
import com.vulpix.api.Utils.Enum.TipoIntegracao;
import com.vulpix.api.Utils.Integracao.Graph;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Repository.IntegracaoRepository;
import com.vulpix.api.Repository.PublicacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.time.OffsetDateTime;
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
    private EmpresaRepository empresaRepository;
    @Autowired
    private TokenService tokenService;

    @Autowired
    public PublicacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long criarContainer(Integracao integracao, Publicacao post) {
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media";

        tokenService.renovarAccessToken(integracao);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image_url", post.getUrlMidia());
        body.add("caption", post.getLegenda());
        body.add("access_token", integracao.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                return Long.parseLong(root.path("id").asText());
            } else {
                throw new RuntimeException("Falha ao criar o container: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro ao criar o container: " + e.getResponseBodyAsString(), e);
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
                .queryParam("access_token", integracao.getAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String id = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
                return id;
            } else {
                throw new RuntimeException("Falha ao criar a publicação: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro ao chamar a API: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<List<GetPublicacaoDto>> buscarPosts(UUID idEmpresa) {
        Optional<Integracao> integracaoOpt = integracaoRepository.findByEmpresaId(idEmpresa);

        if (integracaoOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Integracao integracao = integracaoOpt.get();
        Optional<Empresa> empresaOpt = empresaRepository.findById(idEmpresa);

        if (empresaOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Empresa empresa = empresaOpt.get();
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media?fields=" + Graph.FIELDS + "&access_token=" + integracao.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> rawResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (rawResponseEntity.getBody() == null || rawResponseEntity.getBody().isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponseEntity.getBody());
            JsonNode dataNode = rootNode.path("data");

            if (!dataNode.isArray()) {
                return ResponseEntity.noContent().build();
            }

            List<GetPublicacaoDto> posts = objectMapper.convertValue(dataNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, GetPublicacaoDto.class));

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

            salvarPostNoBanco(resposta, empresa);
            return ResponseEntity.ok(posts);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    public void salvarPostNoBanco(List<Publicacao> posts, Empresa empresa) {
        posts.forEach(post -> {
            post.setEmpresa(empresa);
            Optional<Publicacao> postExistente = publicacaoRepository.findByIdReturned(post.getIdReturned());
            if (postExistente.isEmpty()) {
                publicacaoRepository.save(post);
            }
        });
    }
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void processarPublicacoesAgendadas() {
        List<Publicacao> agendadas = publicacaoRepository.findByStatus(StatusPublicacao.AGENDADA);
        System.out.println(OffsetDateTime.now());
        System.out.println(agendadas);

        for (Publicacao publicacao : agendadas) {
            if (publicacao.getDataPublicacao().isBefore(OffsetDateTime.now())) {
                Integracao integracao = publicacao.getEmpresa().getIntegracoes().stream()
                        .filter(i -> TipoIntegracao.INSTAGRAM.equals(i.getTipo()))
                        .findFirst()
                        .orElse(null);

                if (integracao != null) {
                    Long containerId = criarContainer(integracao, publicacao);
                    String postIdReturned = criarPublicacao(integracao, containerId);
                    publicacao.setIdReturned(postIdReturned);
                    publicacao.setStatus(StatusPublicacao.PUBLICADA);
                    publicacaoRepository.save(publicacao);
                }
            }
        }
    }

}
