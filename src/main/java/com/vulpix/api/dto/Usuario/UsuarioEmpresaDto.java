package com.vulpix.api.dto.Usuario;

import com.vulpix.api.utils.enums.StatusUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEmpresaDto {
    @NotBlank
    private UUID idUsuario;
    @NotBlank
    private String nome;
    @NotBlank
    private String sobrenome;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private StatusUsuario status;
    @NotBlank
    @Size(min = 10, max = 11, message = "Telefone inválido")
    private String telefone;

    @NotBlank
    private UUID idEmpresa;
    @NotBlank
    private String razaoSocial;
    @NotBlank
    private String nomeFantasia;
    @NotBlank
    @CNPJ
    private String cnpj;
    @NotBlank
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @NotBlank
    private String logradouro;
    @NotBlank
    private String numero;
    @NotBlank
    private String bairro;
    @NotBlank
    private String complemento;
    @NotBlank
    private String cidade;
    @NotBlank
    private String estado;
}