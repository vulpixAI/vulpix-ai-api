package com.vulpix.api.Dto.Usuario;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Usuario;

public class UsuarioEmpresaMapper {

    public static UsuarioEmpresaDto toDto(Usuario usuario, Empresa empresa) {

        return UsuarioEmpresaDto.builder()

                .idUsuario(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .status(usuario.getStatus())
                .telefone(usuario.getTelefone())


                .idEmpresa(empresa.getId())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .cnpj(empresa.getCnpj())
                .cep(empresa.getCep())
                .logradouro(empresa.getLogradouro())
                .numero(empresa.getNumero())
                .bairro(empresa.getBairro())
                .complemento(empresa.getComplemento())
                .cidade(empresa.getCidade())
                .estado(empresa.getEstado())
                .build();
    }
}