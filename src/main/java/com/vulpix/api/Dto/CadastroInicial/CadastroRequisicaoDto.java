package com.vulpix.api.Dto.CadastroInicial;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
@Builder
public class CadastroRequisicaoDto {
    @NotBlank
    @Size(min = 3, max = 50)
    private String nome;
    @NotBlank
    @Size(min = 3, max = 100)
    private String sobrenome;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 10, max = 11, message = "Telefone inválido")
    private String telefone;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$", message = "Senha não é segura")
    private String senha;
    @NotBlank
    @Size(min = 3, max = 150)
    private String razaoSocial;
    @NotBlank
    @Size(min = 3, max = 150)
    private String nomeFantasia;
    @NotBlank
    @CNPJ
    private String cnpj;
    @NotBlank
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @NotBlank
    @Size(min=1, max=8)
    private String numero;
    @NotBlank
    @Size(min = 3, max = 150)
    private String logradouro;
    @NotBlank
    @Size(min = 3, max = 100)
    private String cidade;
    @NotBlank
    @Size(min = 2, max = 50)
    private String estado;
    @NotBlank
    @Size(min = 2, max = 50)
    private String bairro;
    @NotBlank
    @Size(min = 2, max = 150)
    private String complemento;
}
