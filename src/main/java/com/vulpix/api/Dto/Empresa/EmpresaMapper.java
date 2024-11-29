package com.vulpix.api.Dto.Empresa;

import com.vulpix.api.Dto.Integracao.IntegracaoDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Entity.Usuario;
import com.vulpix.api.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class EmpresaMapper {

    @Autowired
    private static UsuarioRepository usuarioRepository;

    public static Empresa atualizaEmpresa(EmpresaEditDto dto, Empresa empresa) {
        if (dto == null || empresa == null) return null;

        if (dto.getNomeFantasia() != null && !dto.getNomeFantasia().isEmpty()) {
            empresa.setNomeFantasia(dto.getNomeFantasia());
        }

        if (dto.getCep() != null && !dto.getCep().isEmpty()) {
            empresa.setCep(dto.getCep());
        }

        if (dto.getLogradouro() != null && !dto.getLogradouro().isEmpty()) {
            empresa.setLogradouro(dto.getLogradouro());
        }

        if (dto.getNumero() != null && !dto.getNumero().isEmpty()) {
            empresa.setNumero(dto.getNumero());
        }

        if (dto.getBairro() != null && !dto.getBairro().isEmpty()) {
            empresa.setBairro(dto.getBairro());
        }

        if (dto.getComplemento() != null && !dto.getComplemento().isEmpty()) {
            empresa.setComplemento(dto.getComplemento());
        }

        if (dto.getCidade() != null && !dto.getCidade().isEmpty()) {
            empresa.setCidade(dto.getCidade());
        }

        if (dto.getEstado() != null && !dto.getEstado().isEmpty()) {
            empresa.setEstado(dto.getEstado());
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isEmpty()) {
            Optional<Usuario> usuario = usuarioRepository.findByEmpresa(empresa);
            usuario.ifPresent(u -> {
                u.setTelefone(dto.getTelefone());
                usuarioRepository.save(u);
            });
        }

        return empresa;
    }
}
