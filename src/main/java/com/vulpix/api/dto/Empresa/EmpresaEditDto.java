package com.vulpix.api.dto.Empresa;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class EmpresaEditDto {
    @Size(min = 3, max = 150)@Size(min = 3, max = 150)
    private String nomeFantasia;
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @Size(min = 3, max = 150)
    private String logradouro;
    @Size(min=1, max=8)
    private String numero;
    @Size(min = 3, max = 50)
    private String bairro;
    @Size(min = 3, max = 150)
    private String complemento;
    @Size(min = 3, max = 100)
    private String cidade;
    @Size(min = 3, max = 50)
    private String estado;
    @Size(min = 10, max = 11, message = "Telefone inválido")
    private String telefone;
;}
