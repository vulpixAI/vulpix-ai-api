package com.vulpix.api.service.integracoes.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.vulpix.api.dto.publicacao.GetPublicacaoDto;
import com.vulpix.api.dto.publicacao.Insights.PublicacaoInsightDto;
import com.vulpix.api.dto.publicacao.Insights.ValueDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.exception.exceptions.RequisicaoInvalidaException;
import com.vulpix.api.utils.enums.StatusPublicacao;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.utils.integracao.Graph;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.repository.PublicacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.format.DateTimeParseException;
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

    public void sincronizarPosts(UUID idEmpresa) {
        Optional<Integracao> integracaoOpt = integracaoRepository.findByEmpresaId(idEmpresa);

        if (integracaoOpt.isEmpty()) {
            throw new EntityNotFoundException("Integração não encontrada para a empresa.");
        }

        Integracao integracao = integracaoOpt.get();

        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media?fields=" + Graph.FIELDS +
                "&access_token=" + integracao.getAccessToken();

        Optional<Empresa> empresaOpt = empresaRepository.findById(idEmpresa);
        if (empresaOpt.isEmpty()) {
            throw new EntityNotFoundException("Empresa não encontrada.");
        }

        Empresa empresa = empresaOpt.get();
        List<String> idsPostsAtuais = new ArrayList<>();
        List<Publicacao> todosOsPosts = new ArrayList<>();

        while (url != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() == null || response.getBody().isEmpty()) {
                break;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            try {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");

                if (dataNode.isArray()) {
                    List<GetPublicacaoDto> postsReturn = objectMapper.convertValue(dataNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, GetPublicacaoDto.class));

                    List<Publicacao> posts = postsReturn.stream().map(item -> {
                        Publicacao postDto = new Publicacao();
                        postDto.setIdReturned(item.getId());
                        postDto.setLegenda(item.getLegenda());
                        postDto.setTipoMidia(item.getTipoMidia());
                        postDto.setUrlMidia(item.getUrlMidia());
                        postDto.setDataPublicacao(item.getDataPublicacao());
                        postDto.setLikeCount(item.getLikeCount());
                        idsPostsAtuais.add(item.getId());
                        return postDto;
                    }).collect(Collectors.toList());

                    todosOsPosts.addAll(posts);
                }

                JsonNode pagingNode = rootNode.path("paging");
                if (pagingNode.has("next")) {
                    url = pagingNode.path("next").asText();
                } else {
                    url = null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Sincronizando página da API: " + url);
            System.out.println("Posts sincronizados até agora: " + todosOsPosts.size());
        }

        salvarPostNoBanco(todosOsPosts, empresa);
        excluirPostsRemovidos(idsPostsAtuais, empresa);
    }

    public Page<GetPublicacaoDto> buscarPosts(UUID idEmpresa, int page, int size, String dataInicio, String dataFim) {
        sincronizarPosts(idEmpresa);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPublicacao").descending());

        OffsetDateTime dataFiltroInicio = null;
        OffsetDateTime dataFiltroFim = null;
        if (dataInicio != null && !dataInicio.isEmpty() &&
            dataFim != null && !dataFim.isEmpty()) {
            try {
                dataFiltroInicio = OffsetDateTime.parse(dataInicio + "T00:00:00Z");
                dataFiltroFim = OffsetDateTime.parse(dataFim + "T23:59:59Z");
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de data inválido.");
            }
        }

        Page<Publicacao> publicacoes;

        if (dataFiltroInicio != null && dataFiltroFim != null) {
            if (dataFiltroInicio.isAfter(dataFiltroFim)) throw new RequisicaoInvalidaException("Data de início não pode ser posterior à data de fim.");

            publicacoes = publicacaoRepository.findByEmpresaIdAndDataPublicacaoBetween(idEmpresa, dataFiltroInicio, dataFiltroFim, pageable);
        } else {
            publicacoes = publicacaoRepository.findByEmpresaId(idEmpresa, pageable);
        }

        return publicacoes.map(publicacao -> {
            GetPublicacaoDto dto = new GetPublicacaoDto();
            dto.setId(publicacao.getIdReturned());
            dto.setLegenda(publicacao.getLegenda());
            dto.setTipoMidia(publicacao.getTipoMidia());
            dto.setUrlMidia(publicacao.getUrlMidia());
            dto.setDataPublicacao(publicacao.getDataPublicacao());
            dto.setLikeCount(publicacao.getLikeCount());
            return dto;
        });
    }

    public List<GetPublicacaoDto> buscarPostsSemPaginacao(UUID idEmpresa) {
        List<Publicacao> publicacoes = publicacaoRepository.findByEmpresaId(idEmpresa);

        List<GetPublicacaoDto> postsReturn = publicacoes.stream()
                .map(publicacao -> GetPublicacaoDto.builder()
                        .id(publicacao.getIdReturned())
                        .legenda(publicacao.getLegenda())
                        .tipoMidia(publicacao.getTipoMidia())
                        .urlMidia(publicacao.getUrlMidia())
                        .dataPublicacao(publicacao.getDataPublicacao())
                        .likeCount(publicacao.getLikeCount())
                        .build())
                .collect(Collectors.toList());

        return postsReturn;
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

    public PublicacaoInsightDto buscaInsightPost(String id, UUID idEmpresa) {
        Optional<Publicacao> postEntity = publicacaoRepository.findByIdReturned(id);
        if (postEntity.isEmpty()) return null;

        String idNoInsta = postEntity.get().getIdReturned();

        Optional<Integracao> integracaoOpt = integracaoRepository.findByEmpresaId(idEmpresa);
        if (integracaoOpt.isEmpty()) return null;

        Integracao integracao = integracaoOpt.get();

        String url = UriComponentsBuilder.fromHttpUrl(Graph.BASE_URL)
                .pathSegment(idNoInsta, "insights")
                .queryParam("date_preset", "today")
                .queryParam("metric", "impressions,reach,likes,comments,shares,saved,profile_visits")
                .queryParam("access_token", integracao.getAccessToken())
                .toUriString();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiResponse = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            Map<String, ValueDto> metrics = new HashMap<>();

            JsonNode dataNode = rootNode.path("data");
            if (dataNode.isArray()) {
                for (JsonNode insight : dataNode) {
                    String name = insight.path("name").asText();
                    int value = insight.path("values").get(0).path("value").asInt();

                    metrics.put(name, new ValueDto(value));
                }
            }

            return new PublicacaoInsightDto(metrics);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void excluirPostsRemovidos(List<String> idsPostsAtuais, Empresa empresa) {
        List<Publicacao> postsBanco = publicacaoRepository.findByEmpresaId(empresa.getId());

        List<Publicacao> postsParaExcluir = postsBanco.stream()
                .filter(post -> !idsPostsAtuais.contains(post.getIdReturned()))
                .collect(Collectors.toList());

        publicacaoRepository.deleteAll(postsParaExcluir);
    }
}
