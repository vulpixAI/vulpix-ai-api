package com.vulpix.api.controller;

import com.vulpix.api.dto.Integracao.IntegracaoDto;
import com.vulpix.api.dto.Integracao.IntegracaoMapper;
import com.vulpix.api.dto.Integracao.IntegracaoUpdateDto;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.IntegracaoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/integracoes")
@Tag(name = "Controller de Integração")
public class IntegracaoController {

    @Autowired
    private IntegracaoService integracaoService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private EmpresaHelper empresaHelper;

    @Operation(summary = "Habilita uma nova integração",
            description = "Cria uma nova integração para a empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Integração habilitada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"tipo\":\"INSTAGRAM\",\"id\":\"1\",\"fkEmpresa\":\"empresa-1\"}")
                            })),
            @ApiResponse(responseCode = "400", description = "Dados da nova integração inválidos.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Dados inválidos para a nova integração.\" }"))),
            @ApiResponse(responseCode = "409", description = "Já existe uma integração ativa do mesmo tipo.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Integração do mesmo tipo já está ativa.\" }")))
    })
    @PostMapping
    public ResponseEntity<Integracao> habilitar(@RequestBody IntegracaoDto novaIntegracao) {
        if (novaIntegracao == null) return ResponseEntity.badRequest().build();

        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao integracao = IntegracaoMapper.criaEntidadeIntegracao(novaIntegracao, empresa);

        Optional<Integracao> integracaoAtiva = integracaoService.findByEmpresaAndTipo(empresa, integracao.getTipo());
        if (integracaoAtiva.isPresent()) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(201).body(integracaoService.save(integracao));
    }

    @Operation(summary = "Atualiza uma integração existente",
            description = "Atualiza os dados de uma integração para a empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integração atualizada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"tipo\":\"INSTAGRAM\",\"id\":\"1\",\"status\":\"atualizado\"}"))),
            @ApiResponse(responseCode = "404", description = "Integração não encontrada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Integração não encontrada.\" }")))
    })
    @PatchMapping
    public ResponseEntity<Integracao> atualizar(@RequestBody IntegracaoUpdateDto integracaoAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Optional<Integracao> integracaoExistente = integracaoService.findByEmpresaAndTipo(empresa, TipoIntegracao.INSTAGRAM);

        if (integracaoExistente.isEmpty()) return ResponseEntity.status(404).build();

        Integracao integracao = IntegracaoMapper.criaEntidadeAtualizada(empresa, integracaoAtualizada);

        Integracao integracaoAtualizadaSalva = integracaoService.atualizaIntegracao(integracaoExistente.get().getId(), integracao);
        return ResponseEntity.status(200).body(integracaoAtualizadaSalva);
    }

    @Operation(summary = "Deleta uma integração existente",
            description = "Remove uma integração da empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Integração deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Integração não encontrada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Integração não encontrada.\" }")))
    })
    @DeleteMapping
    public ResponseEntity<Void> deletar() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Optional<Integracao> integracaoExistente = integracaoService.findByEmpresaAndTipo(empresa, TipoIntegracao.INSTAGRAM);

        if (integracaoExistente.isEmpty()) return ResponseEntity.status(404).build();

        integracaoService.deleteById(integracaoExistente.get().getId());
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Verifica se a empresa possui integração",
            description = "Verifica se a empresa do usuário autenticado possui uma integração ativa do tipo especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"possuiIntegracao\":true}"))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @GetMapping("/possui-integracao")
    public boolean possuiIntegracao() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Optional<Integracao> integracaoExistente = integracaoService.findByEmpresaAndTipo(empresa, TipoIntegracao.INSTAGRAM);

        return integracaoExistente.isPresent();
    }

    @GetMapping()
    public ResponseEntity<Integracao> retornaIntegracao() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao retorno = integracaoService.retornaIntegracao(empresa);

        if (retorno != null) return ResponseEntity.status(200).body(retorno);
        return ResponseEntity.status(404).build();
    }
}
