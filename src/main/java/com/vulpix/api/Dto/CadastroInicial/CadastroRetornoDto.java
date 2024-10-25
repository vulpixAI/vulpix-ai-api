package com.vulpix.api.Dto.CadastroInicial;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CadastroRetornoDto {
    private UUID id;
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private Boolean status;
    private EmpresaDto empresa;

    @Data
    @Builder
    public static class EmpresaDto {
        private UUID idEmpresa;
        private String razaoSocial;
        private String nomeFantasia;
        private String cnpj;
        private EnderecoDto endereco;

        @Data
        @Builder
        public static class EnderecoDto {
            private String cep;
            private String logradouro;
            private String numero;
            private String bairro;
            private String complemento;
            private String cidade;
            private String estado;
        }
    }
}
