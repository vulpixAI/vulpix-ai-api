package com.vulpix.api.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Publicacao;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.service.integracoes.graph.PublicacaoService;
import com.vulpix.api.utils.enums.StatusPublicacao;
import com.vulpix.api.utils.integracao.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@DisplayName("Testes da Classe PublicacaoService")
class PublicacaoServiceTest {

    @Mock
    private IntegracaoRepository integracaoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PublicacaoService publicacaoService;

    private Empresa empresa;
    private Integracao integracao;
    private Publicacao publicacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        integracao = new Integracao();
        integracao.setIgUserId("userId");
        integracao.setAccessToken("accessToken");
        integracao.setEmpresa(empresa);

        publicacao = new Publicacao();
        publicacao.setIdReturned("12345");
        publicacao.setStatus(StatusPublicacao.AGENDADA);
    }

    @Test
    @DisplayName("Dado que a publicação foi criada, então deve retornar o id da publicação")
    void testCriarContainer_Success() {
        String url = Graph.BASE_URL + integracao.getIgUserId() + "/media";
        String mockResponse = "{\"id\":\"67890\"}";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Long containerId = publicacaoService.criarContainer(integracao, publicacao);

        assertEquals(67890, containerId);
    }

    @Test
    @DisplayName("Dado que a publicação não foi criada, então deve retornar null")
    void testCriarPublicacao_Success() {
        String containerId = "67890";
        String mockResponse = "{\"id\":\"54321\"}";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        String postId = publicacaoService.criarPublicacao(integracao, Long.parseLong(containerId));

        assertEquals("54321", postId);
    }

}