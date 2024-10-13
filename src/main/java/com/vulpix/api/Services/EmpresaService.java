package com.vulpix.api.Services;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Services.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Services.Usuario.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmpresaService {
    @Autowired
     EmpresaRepository empresaRepository;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private UsuarioService usuarioService;

    public boolean empresaExistePorRazaoSocialECnpj(String razaoSocial, String cnpj) {
        return empresaRepository.findByRazaoSocialAndCnpj(razaoSocial, cnpj).isPresent();
    }

    public Empresa salvarEmpresa(Empresa novaEmpresa) {
        if (!empresaExistePorRazaoSocialECnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj()))
            return empresaRepository.save(novaEmpresa);
        return null;
    }

    public Empresa atualizarEmpresa(UUID id, Empresa empresaAtualizada) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        if (empresaOpt.isEmpty()) {
            return null;
        }

        Empresa empresaExistente = empresaOpt.get();
        atualizarDadosEmpresa(empresaExistente, empresaAtualizada);

        return empresaRepository.save(empresaExistente);
    }

    private void atualizarDadosEmpresa(Empresa empresaExistente, Empresa empresaAtualizada) {
        if (empresaAtualizada.getCep() != null && !empresaAtualizada.getCep().isEmpty()) {
            empresaExistente.setCep(empresaAtualizada.getCep());
        }
        if (empresaAtualizada.getLogradouro() != null && !empresaAtualizada.getLogradouro().isEmpty()) {
            empresaExistente.setLogradouro(empresaAtualizada.getLogradouro());
        }
        if (empresaAtualizada.getNumero() != null && !empresaAtualizada.getNumero().isEmpty()) {
            empresaExistente.setNumero(empresaAtualizada.getNumero());
        }
        if (empresaAtualizada.getBairro() != null && !empresaAtualizada.getBairro().isEmpty()) {
            empresaExistente.setBairro(empresaAtualizada.getBairro());
        }
        if (empresaAtualizada.getEstado() != null && !empresaAtualizada.getEstado().isEmpty()) {
            empresaExistente.setEstado(empresaAtualizada.getEstado());
        }
        if (empresaAtualizada.getCidade() != null && !empresaAtualizada.getCidade().isEmpty()) {
            empresaExistente.setCidade(empresaAtualizada.getCidade());
        }
    }

    public Empresa buscarEmpresaPeloUsuario() {
        UUID usuarioId = usuarioService.retornaIdUsuarioLogado();

        Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioId(usuarioId);

        if (empresaOpt.isPresent()) {
            return empresaOpt.get();
        }

        throw new EntityNotFoundException("Empresa não encontrada para o usuário autenticado");
    }
}
