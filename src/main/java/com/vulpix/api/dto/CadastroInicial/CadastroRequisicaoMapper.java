package com.vulpix.api.dto.CadastroInicial;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;

import java.time.LocalDateTime;

public class CadastroRequisicaoMapper {
    public static Usuario criaEntidadeUsuario(CadastroRequisicaoDto dto){
        if (dto == null) return null;

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .sobrenome(dto.getSobrenome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .status(true)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return usuario;
    }

    public static Empresa criaEntidadeEmpresa(CadastroRequisicaoDto dto, Usuario responsavel){
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

    public static CadastroRetornoDto retornoCadastro(Usuario usuario, Empresa empresa) {

        CadastroRetornoDto.EmpresaDto.EnderecoDto endereco = CadastroRetornoDto.EmpresaDto.EnderecoDto.builder()
                .cep(empresa.getCep())
                .bairro(empresa.getBairro())
                .logradouro(empresa.getLogradouro())
                .numero(empresa.getNumero())
                .complemento(empresa.getComplemento())
                .cidade(empresa.getCidade())
                .estado(empresa.getEstado())
                .build();

        CadastroRetornoDto.EmpresaDto empresaRes = CadastroRetornoDto.EmpresaDto.builder()
                .idEmpresa(empresa.getId())
                .cnpj(empresa.getCnpj())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .endereco(endereco)
                .build();

        return CadastroRetornoDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .empresa(empresaRes)
                .build();
    }


}
