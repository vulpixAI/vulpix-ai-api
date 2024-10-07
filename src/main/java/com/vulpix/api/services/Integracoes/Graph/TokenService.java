package com.vulpix.api.services.Integracoes.Graph;

import com.vulpix.api.Enum.TipoIntegracao;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TokenService {
    private final String BASE_URL_TOKEN = "https://graph.facebook.com/oauth/access_token";
    @Autowired
    private IntegracaoRepository integracaoRepository;
    @Autowired
    private RestTemplate restTemplate;
    public Integracao renovarAccessToken(Integracao integracao){
        if (!integracao.getStatus() || integracao.getTipo() != TipoIntegracao.INSTAGRAM) return integracao;

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL_TOKEN)
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", integracao.getClientId())
                .queryParam("client_secret", integracao.getClientSecret())
                .queryParam("fb_exchange_token", integracao.getAccessToken())
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String novoAccessToken = (String) response.getBody().get("access_token");
                Integer expiresIn = (Integer) response.getBody().get("expires_in");
                System.out.println("Novo access_token: "+novoAccessToken);

                integracao.setAccessToken(novoAccessToken);
                integracao.setAccessTokenExpireDate(LocalDateTime.now().plusSeconds(expiresIn));

                System.out.println("Access Token renovado com sucesso!");
                return integracao;
            } else {
                throw new RuntimeException("Falha ao renovar o access token: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro ao chamar a API: " + e.getMessage(), e);
        }
    }

}
