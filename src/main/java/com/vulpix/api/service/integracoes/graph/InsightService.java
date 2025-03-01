package com.vulpix.api.service.integracoes.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.PostInsights;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.InsightRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InsightService {
    private final RestTemplate restTemplate;
    @Autowired
    private IntegracaoRepository integracaoRepository;
    @Autowired
    private PublicacaoRepository publicacaoRepository;
    @Autowired
    private InsightRepository insightRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private TokenService tokenService;

    @Autowired
    public InsightService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void fetchInsightsBatch(Integracao integracao) {
        List<Publicacao> publicacoes = publicacaoRepository.findByEmpresaId(integracao.getEmpresa().getId());

        List<Map<String, String>> batchRequests = new ArrayList<>();
        for (Publicacao publicacao : publicacoes) {
            Map<String, String> operation = new HashMap<>();
            operation.put("method", "GET");
            operation.put("relative_url", publicacao.getIdReturned() + "/insights?metric=impressions,saved,likes,comments,shares,profile_visits,follows");
            batchRequests.add(operation);
        }

        System.out.println("Requisição: \n"+batchRequests);

        String url = "https://graph.facebook.com/v17.0";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("access_token", integracao.getAccessToken());
        requestBody.put("batch", batchRequests);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                for (int i = 0; i < rootNode.size(); i++) {
                    JsonNode resultNode = rootNode.get(i);
                    if (resultNode.has("body")) {
                        JsonNode insightsNode = objectMapper.readTree(resultNode.get("body").asText());

                        String postId = publicacoes.get(i).getIdReturned();

                        salvarInsights(insightsNode, postId);
                        System.out.println("Salvando insight do post: "+postId);
                    }
                }
            } else {
                throw new RuntimeException("Erro ao buscar insights: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro na requisição batch: " + e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar resposta da API", e);
        }
    }
    private void salvarInsights(JsonNode insightsNode, String postId) {
        PostInsights postInsights = new PostInsights();
        System.out.println("Resposta recebida: " + insightsNode.toPrettyString());

        JsonNode dataNode = insightsNode.get("data");
        if (dataNode != null && dataNode.isArray()) {
            dataNode.forEach(metric -> {
                String name = metric.path("name").asText();
                int value = extrairMetricas(metric);

                switch (name) {
                    case "impressions":
                        postInsights.setImpressions(value);
                        break;
                    case "saved":
                        postInsights.setSaves(value);
                        break;
                    case "likes":
                        postInsights.setLikes(value);
                        break;
                    case "comments":
                        postInsights.setComments(value);
                        break;
                    case "shares":
                        postInsights.setShares(value);
                        break;
                    case "profile_visits":
                        postInsights.setProfileVisits(value);
                        break;
                    case "follows":
                        postInsights.setFollows(value);
                        break;
                    default:
                        System.out.println("Métrica desconhecida: " + name);
                }
            });
        } else {
            System.out.println("O nó 'data' está ausente ou não é um array para o postId: " + postId);
            return;
        }

        postInsights.setCreatedAt(OffsetDateTime.now());

        Publicacao publicacao = publicacaoRepository.findByIdReturned(postId)
                .orElseThrow(() -> new RuntimeException("Publicacao não encontrada para postId: " + postId));

        postInsights.setPublicacao(publicacao);

        insightRepository.save(postInsights);
    }

    private int extrairMetricas(JsonNode metric) {
        JsonNode valuesNode = metric.path("values");
        if (valuesNode.isArray() && valuesNode.size() > 0) return valuesNode.get(0).path("value").asInt(0);

        return 0;
    }
}
