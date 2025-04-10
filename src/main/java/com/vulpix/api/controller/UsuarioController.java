package com.vulpix.api.controller;

import com.vulpix.api.dto.autenticacao.LoginResponse;
import com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto;
import com.vulpix.api.dto.usuario.AtualizarSenhaDto;
import com.vulpix.api.dto.usuario.UsuarioEmpresaDto;
import com.vulpix.api.dto.usuario.UsuarioLoginDto;
import com.vulpix.api.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/usuarios")
@Tag(name = "Usuário")
public interface UsuarioController {
    @Operation(summary = "Cadastrar um novo usuário", description = "Realiza o cadastro de um novo usuário e sua empresa associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário e empresa cadastrados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João\", \"empresa\": { \"nome\": \"Empresa Exemplo\" }}")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Conflito ao salvar a empresa.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 409, \"detail\": \"Empresa já cadastrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PostMapping
    ResponseEntity<CadastroRetornoDto> cadastrar(@RequestBody CadastroRequisicaoDto cadastroRequisicaoDto);

    @Operation(summary = "Autenticar um usuário", description = "Realiza a autenticação de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"token\": \"abcdefg1234567\", \"usuarioId\": \"123e4567-e89b-12d3-a456-426614174000\" }")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Falha na autenticação.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 401, \"detail\": \"E-mail ou senha inválido(s).\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> autenticar(@RequestBody UsuarioLoginDto usuario);

    @Operation(
            summary = "Buscar dados do usuário e da empresa associada",
            description = "Retorna os dados do usuário juntamente com os dados da empresa associada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = com.vulpix.api.dto.autenticacao.UsuarioTokenDto.class),
                            examples = @ExampleObject(value = "{ \"userId\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João\", \"email\": \"joao@email.com\", \"token\": \"jwt-token\", \"status\": \"ATIVO\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "MFA requerido para concluir autenticação.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = com.vulpix.api.dto.autenticacao.MfaRequiredResponse.class),
                            examples = @ExampleObject(value = "{ \"status\": \"MFA_REQUIRED\", \"email\": \"joao@email.com\" }")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Usuário ou empresa não encontrados.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping
    ResponseEntity<UsuarioEmpresaDto> buscarUsuarioComEmpresa(@RequestParam(required = false) @Nullable String email);

    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João Atualizado\" }")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Usuário não encontrado.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PutMapping
    ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuarioAtualizado);

    @Operation(
            summary = "Atualizar senha do usuário",
            description = "Atualiza a senha de um usuário autenticado, verificando a senha atual antes de aplicar a nova."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(
                    responseCode = "401",
                    description = "Senha atual fornecida está incorreta.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 401, \"detail\": \"Senha atual fornecida está incorreta.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Usuário não encontrado.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PatchMapping("/senha")
    ResponseEntity<Void> atualizarSenha(@RequestBody AtualizarSenhaDto atualizarSenhaDto);

    @Operation(summary = "Remover usuário", description = "Remove um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Usuário não encontrado.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Void> remover(@Parameter(description = "Usuário a ser removido", required = true) @PathVariable UUID id);

}