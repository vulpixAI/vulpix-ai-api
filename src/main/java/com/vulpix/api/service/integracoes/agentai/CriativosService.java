package com.vulpix.api.service.integracoes.agentai;

import com.vulpix.api.dto.agent.PublicacaoGeradaResponse;
import com.vulpix.api.dto.agent.PublicacaoGeradaRetorno;
import com.vulpix.api.dto.criativo.CriativoMapper;
import com.vulpix.api.dto.criativo.CriativoRequisicaoDto;
import com.vulpix.api.dto.criativo.CriativoResponseDto;
import com.vulpix.api.dto.criativo.CriativoUnitDto;
import com.vulpix.api.entity.Criativo;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.exception.exceptions.RequisicaoInvalidaException;
import com.vulpix.api.repository.CriativoRepository;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

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

    @Value("${ip.agent}")
    private String ipAgent;

    public PublicacaoGeradaRetorno buscaCriativos(String prompt, String userRequest) {
        String URL = "http://" + ipAgent + ":5000/generate-content";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("user_request", userRequest);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Requisição: " + requestBody);

        try {
            ResponseEntity<PublicacaoGeradaResponse> responseEntity = restTemplate.exchange(
                    URL,
                    HttpMethod.POST,
                    requestEntity,
                    PublicacaoGeradaResponse.class
            );

            PublicacaoGeradaResponse response = responseEntity.getBody();
            PublicacaoGeradaRetorno retorno = PublicacaoGeradaRetorno.builder()
                    .legenda(response != null ? response.getCaption() : null)
                    .build();

            if (response != null && response.getImage_urls() != null) {
                List<String> imageUrls = response.getImage_urls();

                if (imageUrls.size() > 0) retorno.setImagem1(imageUrls.get(0));
                if (imageUrls.size() > 1) retorno.setImagem2(imageUrls.get(1));
                if (imageUrls.size() > 2) retorno.setImagem3(imageUrls.get(2));
                if (imageUrls.size() > 3) retorno.setImagem4(imageUrls.get(3));

                for (String imageUrl : imageUrls) {
                    salvaCriativos(imageUrl, userRequest);
                }
            }

            return retorno;

        } catch (HttpServerErrorException e) {
            System.err.println("Erro ao enviar requisição de gerar post para Agent: " + e.getMessage());

            return PublicacaoGeradaRetorno.builder()
                    .legenda(null)
                    .imagem1(null)
                    .imagem2(null)
                    .imagem3(null)
                    .imagem4(null)
                    .build();
        }
    }

    public String buscaLegenda(String prompt, String userRequest) {
        String URL = "http://" + ipAgent + ":5000/generate-caption";
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

    public Page<CriativoResponseDto> buscaCriativosGerados(Empresa empresa, int page, int size, String dataInicio, String dataFim) {
        int sizeReal = size * 4;
        Pageable pageable = PageRequest.of(page, sizeReal, Sort.by("createdAt").descending());

        OffsetDateTime dataFiltroInicioOffset = null;
        OffsetDateTime dataFiltroFimOffset = null;

        if (dataInicio != null && !dataInicio.isEmpty() && dataFim != null && !dataFim.isEmpty()) {
            try {
                dataFiltroInicioOffset = OffsetDateTime.parse(dataInicio + "T00:00:00Z");
                dataFiltroFimOffset = OffsetDateTime.parse(dataFim + "T23:59:59Z");
            } catch (DateTimeParseException e) {
                throw new RequisicaoInvalidaException("Formato de data inválido.");
            }
        }

        Page<Criativo> criativosEntity;

        if (dataFiltroInicioOffset != null && dataFiltroFimOffset != null) {
            LocalDateTime dataFiltroInicio = dataFiltroInicioOffset.toLocalDateTime();
            LocalDateTime dataFiltroFim = dataFiltroFimOffset.toLocalDateTime();

            if (dataFiltroInicio.isAfter(dataFiltroFim)) {
                throw new RequisicaoInvalidaException("Data de início não pode ser posterior à data de fim.");
            }

            criativosEntity = criativoRepository.findAllByEmpresaAndCreatedAtBetweenOrderByCreatedAtDesc(empresa, dataFiltroInicio, dataFiltroFim, pageable);
        } else {
            criativosEntity = criativoRepository.findAllByEmpresaOrderByCreatedAtDesc(empresa, pageable);
        }

        List<Criativo> criativosList = criativosEntity.getContent();
        List<CriativoResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < criativosList.size(); i += 4) {
            CriativoResponseDto dto = new CriativoResponseDto();
            List<CriativoUnitDto> images = new ArrayList<>();

            for (int j = 0; j < 4 && (i + j) < criativosList.size(); j++) {
                Criativo criativo = criativosList.get(i + j);
                images.add(CriativoUnitDto.builder()
                        .id(criativo.getId())
                        .image_url(criativo.getImageUrl())
                        .build());
            }

            dto.setImages(images);
            dto.setPrompt(criativosList.get(i).getPrompt());
            responseList.add(dto);
        }

        long totalConjuntos = (criativosEntity.getTotalElements() + 3) / 4;

        return new PageImpl<>(responseList, PageRequest.of(page, size), totalConjuntos);
    }

    public CriativoRequisicaoDto buscaPorId(UUID id) {
        Criativo criativo = criativoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Criativo não encontrado."));

        return CriativoRequisicaoDto.builder()
                .imageUrl(criativo.getImageUrl())
                .prompt(criativo.getPrompt())
                .build();
    }
}