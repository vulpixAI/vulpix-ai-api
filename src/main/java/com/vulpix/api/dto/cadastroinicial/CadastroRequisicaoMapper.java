package com.vulpix.api.dto.cadastroinicial;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.utils.enums.StatusUsuario;

import java.time.LocalDateTime;

public class CadastroRequisicaoMapper {
    public static Usuario criaEntidadeUsuario(com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoDto dto) {
        if (dto == null) return null;

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .sobrenome(dto.getSobrenome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .status(StatusUsuario.AGUARDANDO_PAGAMENTO)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return usuario;
    }

    public static Empresa criaEntidadeEmpresa(com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoDto dto, Usuario responsavel) {
        if (dto == null || responsavel == null) return null;

        Empresa empresa = Empresa.builder()
                .razaoSocial(dto.getRazaoSocial())
                .nomeFantasia(dto.getNomeFantasia())
                .cnpj(dto.getCnpj())
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .bairro(dto.getBairro())
                .complemento(dto.getComplemento())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .usuario(responsavel)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return empresa;
    }

    public static com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto retornoCadastro(Usuario usuario, Empresa empresa) {

        com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto.EmpresaDto.EnderecoDto endereco = com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto.EmpresaDto.EnderecoDto.builder()
                .cep(empresa.getCep())
                .bairro(empresa.getBairro())
                .logradouro(empresa.getLogradouro())
                .numero(empresa.getNumero())
                .complemento(empresa.getComplemento())
                .cidade(empresa.getCidade())
                .estado(empresa.getEstado())
                .build();

        com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto.EmpresaDto empresaRes = com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto.EmpresaDto.builder()
                .idEmpresa(empresa.getId())
                .cnpj(empresa.getCnpj())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .endereco(endereco)
                .build();

        return com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .status(usuario.getStatus().name())
                .empresa(empresaRes)
                .build();
    }
}