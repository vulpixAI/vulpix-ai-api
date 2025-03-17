package com.vulpix.api.api;

import com.vulpix.api.dto.Integracao.IntegracaoDto;
import com.vulpix.api.dto.Integracao.IntegracaoUpdateDto;
import com.vulpix.api.entity.Integracao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/integracoes")
@Tag(name = "Integração")
public interface IntegracaoApi {
    @Operation(summary = "Habilita uma nova integração",
            description = "Cria uma nova integração para a empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Integração habilitada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"tipo\":\"INSTAGRAM\",\"id\":\"1\",\"fkEmpresa\":\"empresa-1\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados da nova integração inválidos.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 400, \"detail\": \"Dados inválidos para a nova integração.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Já existe uma integração ativa do mesmo tipo.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 409, \"detail\": \"Integração do mesmo tipo já está ativa.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PostMapping
    ResponseEntity<Integracao> habilitar(@RequestBody IntegracaoDto novaIntegracao);

    @Operation(summary = "Atualiza uma integração existente",
            description = "Atualiza os dados de uma integração para a empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integração atualizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"tipo\":\"INSTAGRAM\",\"id\":\"1\",\"status\":\"atualizado\"}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Integração não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Integração não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PatchMapping
    ResponseEntity<Integracao> atualizar(@RequestBody IntegracaoUpdateDto integracaoAtualizada);

    @Operation(summary = "Deleta uma integração existente",
            description = "Remove uma integração da empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Integração excluída com sucesso.", content = @Content(examples = @ExampleObject())),
            @ApiResponse(responseCode = "404", description = "Integração não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Integração não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Void> deletar();

    @Operation(summary = "Verifica se a empresa possui integração",
            description = "Verifica se a empresa do usuário autenticado possui uma integração ativa do tipo especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"possuiIntegracao\":true}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 404, \"detail\": \"Empresa não encontrada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping("/possui-integracao")
    boolean possuiIntegracao();
}