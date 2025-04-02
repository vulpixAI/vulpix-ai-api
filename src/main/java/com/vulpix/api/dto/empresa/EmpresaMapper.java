package com.vulpix.api.dto.empresa;

import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmpresaMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Empresa atualizaEmpresa(EmpresaEditDto dto, Empresa empresa) {
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
