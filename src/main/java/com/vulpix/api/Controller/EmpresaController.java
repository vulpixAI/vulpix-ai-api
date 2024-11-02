package com.vulpix.api.Controller;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
@Tag(name= "Controller de Empresa")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Operation(summary = "Atualiza dados da empresa do usuário autenticado", description = "Atualiza os dados da empresa associada ao usuário atualmente autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"nome\": \"Empresa Atualizada\" }"))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PatchMapping
    public ResponseEntity<Empresa> atualizar(@RequestBody Empresa empresaAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Empresa empresaAtualizadaSalva = empresaService.atualizarEmpresa(empresa, empresaAtualizada);
        return ResponseEntity.ok(empresaAtualizadaSalva);
    }

    @Operation(summary = "Cadastra um novo formulário para a empresa do usuário autenticado", description = "Adiciona um novo formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário cadastrado com sucesso.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"formularioId\": \"12345\", \"descricao\": \"Novo formulário cadastrado\" }"))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PostMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> cadastrarFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        FormularioRequisicaoDto retorno = empresaService.cadastrarFormulario(empresa, formulario);
        return ResponseEntity.ok(retorno);
    }

    @Operation(summary = "Busca o formulário da empresa do usuário autenticado", description = "Retorna o formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário encontrado com sucesso.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"formularioId\": \"12345\", \"descricao\": \"Detalhes do formulário\" }"))),
            @ApiResponse(responseCode = "404", description = "Formulário não encontrado.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro: Formulário não encontrado.\" }")))
    })
    @GetMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> buscaFormulario() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto formularioResponse = empresaService.buscaFormulario(empresa);

        if (formularioResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(formularioResponse);
    }

    @Operation(summary = "Atualiza o formulário da empresa do usuário autenticado", description = "Atualiza os dados do formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"formularioId\": \"12345\", \"descricao\": \"Formulário atualizado\" }"))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PutMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> atualizaFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        FormularioRequisicaoDto retorno = empresaService.atualizaFormulario(empresa, formulario);
        return ResponseEntity.ok(retorno);
    }
}