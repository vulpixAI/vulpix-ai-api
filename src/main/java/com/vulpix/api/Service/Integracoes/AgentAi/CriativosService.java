package com.vulpix.api.Service.Integracoes.AgentAi;

import com.vulpix.api.Dto.Agent.PublicacaoGeradaResponse;
import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.Dto.Criativo.CriativoMapper;
import com.vulpix.api.Dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.Entity.Criativo;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.CriativoRepository;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CriativosService {

    @Autowired
    private CriativoRepository criativoRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;

    public PublicacaoGeradaRetorno buscaCriativos(String prompt, String userRequest) {
            PublicacaoGeradaRetorno retorno = PublicacaoGeradaRetorno.builder()
                    .legenda("Geração de post mockado afim de testes")
                    .imagem1("https://scontent.cdninstagram.com/v/t51.2885-15/465951352_1059539495917616_1430117094878724406_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=18de74&_nc_ohc=S48gn8AeOHYQ7kNvgF9Hco1&_nc_zt=23&_nc_ht=scontent.cdninstagram.com&edm=AM6HXa8EAAAA&oh=00_AYB47rC-kUhXb06kskfwXlmTH4hsJBOWkUGeeSx1xgB9Mg&oe=673AA1BB")
                    .imagem2("https://scontent.cdninstagram.com/v/t51.2885-15/465802927_1628702564693653_5064327801977097765_n.jpg?_nc_cat=101&ccb=1-7&_nc_sid=18de74&_nc_ohc=2-gSja8DQ40Q7kNvgEgzQA1&_nc_zt=23&_nc_ht=scontent.cdninstagram.com&edm=AM6HXa8EAAAA&oh=00_AYCzPiQUi2SaaD8nFg040XO4HdsUWhPrUWXGsC1rqLaVjQ&oe=673AA99D")
                    .imagem3("https://scontent.cdninstagram.com/v/t51.2885-15/465581512_1624285551819587_6016394554618162900_n.jpg?_nc_cat=102&ccb=1-7&_nc_sid=18de74&_nc_ohc=suA41zCDFtsQ7kNvgF5zZCj&_nc_zt=23&_nc_ht=scontent.cdninstagram.com&edm=AM6HXa8EAAAA&oh=00_AYDwzzLS53E1ksa_QG6qqGVVbtfYn9WtnyKFdVg3sYEFqg&oe=673AA4CC")
                    .imagem4("https://scontent.cdninstagram.com/v/t51.2885-15/465612471_1832441967561171_5765861591217010485_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=18de74&_nc_ohc=0UotgaD99foQ7kNvgH834VS&_nc_zt=23&_nc_ht=scontent.cdninstagram.com&edm=AM6HXa8EAAAA&oh=00_AYAiwgFpHGZ5x8S0XHoTd7UZ8YgkzgAaHS1HWf9ZCRnr7w&oe=673A994B")
                    .build();

            return retorno;
    }

    public String buscaLegenda(String prompt, String userRequest) {
        String URL = "http://127.0.0.1:5000/generate-caption";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("user_request", userRequest);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Requisição: " + requestBody);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String response = responseEntity.getBody();
            return response;

        } catch (HttpServerErrorException e) {
            System.err.println("Erro no servidor Python ao gerar legenda: " + e.getMessage());
            return null;
        }
    }

    public void salvaCriativos(String imageUrl, String prompt) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        CriativoRequisicaoDto dto = CriativoRequisicaoDto.builder().imageUrl(imageUrl).prompt(prompt).build();

        Criativo criativo = CriativoMapper.criaEntidadeCriativo(dto, empresa);
        criativoRepository.save(criativo);
    }
}
