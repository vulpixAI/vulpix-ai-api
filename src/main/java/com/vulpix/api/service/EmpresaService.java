package com.vulpix.api.service;
import com.vulpix.api.dto.Empresa.EmpresaEditDto;
import com.vulpix.api.dto.Empresa.EmpresaMapper;
import com.vulpix.api.entity.ConfigPrompt;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.exception.exceptions.ConflitoException;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.repository.ConfigRepository;
import com.vulpix.api.repository.EmpresaRepository;
import com.vulpix.api.service.integracoes.agentai.CriativosService;
import com.vulpix.api.service.integracoes.agentai.PromptService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.enums.StatusUsuario;
import com.vulpix.api.utils.JsonConverter;
import com.vulpix.api.dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private PromptService promptService;

    @Autowired
    private CriativosService criativosService;

    @Autowired
    private EmpresaMapper empresaMapper;

    @Autowired
    private EmpresaEditDto empresaEditDto;

    public Empresa buscaPorId(UUID id){
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        if (empresaOpt.isEmpty()) return null;

        return empresaOpt.get();
    }

    public boolean empresaExistePorRazaoSocialECnpj(String razaoSocial, String cnpj) {
        return empresaRepository.findByRazaoSocialAndCnpj(razaoSocial, cnpj).isPresent();
    }

    public Empresa salvarEmpresa(Empresa novaEmpresa) {
        if (empresaExistePorRazaoSocialECnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O CNPJ informado já está cadastrado no sistema.");
        }
        return empresaRepository.save(novaEmpresa);
    }

    public EmpresaEditDto atualizarEmpresa(Empresa empresa, EmpresaEditDto empresaAtualizada) {
        if (empresa == null) return null;

        Empresa empresaAtualizadaEntity = empresaMapper.atualizaEmpresa(empresaAtualizada, empresa);

        empresaRepository.save(empresaAtualizadaEntity);
        return empresaAtualizada;
    }

    public FormularioRequisicaoDto cadastrarFormulario(Empresa empresa, FormularioRequisicaoDto formulario) {
        ConfigPrompt configPrompt = new ConfigPrompt();

        String jsonForm = JsonConverter.toJson(formulario);
        configPrompt.setForm(jsonForm);

        configRepository.findByEmpresaId(empresa.getId()).orElseThrow(() -> new ConflitoException("Essa empresa já possui suas informações cadastradas."));

        configPrompt.setEmpresa(empresa);
        configRepository.save(configPrompt);

        salvaPrompt(empresa);
        usuarioService.atualizaStatus(empresa, StatusUsuario.CADASTRO_FINALIZADO);

        return formulario;
    }

    public FormularioRequisicaoDto buscaFormulario(Empresa empresa) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new NaoEncontradoException("ConfigPrompt não encontrado."));;

        return JsonConverter.fromJson(configPrompt.getForm());
    }

    public FormularioRequisicaoDto atualizaFormulario(Empresa empresa, FormularioRequisicaoDto formulario) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new NaoEncontradoException("ConfigPrompt não encontrado."));;

        String jsonForm = JsonConverter.toJson(formulario);
        configPrompt.setForm(jsonForm);

        configRepository.save(configPrompt);
        salvaPrompt(empresa);
        return formulario;
    }

    public void salvaPrompt(Empresa empresa) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RuntimeException("ConfigPrompt não encontrado"));

        String prompt = promptService.salvarPrompt(configPrompt);

        configPrompt.setPrompt(prompt);
        configRepository.save(configPrompt);
    }

    public PublicacaoGeradaRetorno buscaCriativos(Empresa empresa, String userRequest){
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RuntimeException("ConfigPrompt não encontrado"));

        if (configPrompt.getPrompt().isEmpty()) {
            salvaPrompt(empresa);
        }

        PublicacaoGeradaRetorno retorno = criativosService.buscaCriativos(configPrompt.getPrompt(), userRequest);
        return retorno;
    }

    public String buscaLegenda(Empresa empresa, String userRequest) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RuntimeException("ConfigPrompt não encontrado"));

        String legenda = criativosService.buscaLegenda(configPrompt.getPrompt(), userRequest);
        return legenda;
    }
}