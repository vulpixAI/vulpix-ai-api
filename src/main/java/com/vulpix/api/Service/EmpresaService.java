package com.vulpix.api.Service;

import com.vulpix.api.Dto.Empresa.EmpresaEditDto;
import com.vulpix.api.Dto.Empresa.EmpresaMapper;
import com.vulpix.api.Entity.ConfigPrompt;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.ConfigRepository;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Service.Integracoes.AgentAi.CriativosService;
import com.vulpix.api.Service.Integracoes.AgentAi.PromptService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Utils.Enum.StatusUsuario;
import com.vulpix.api.Utils.JsonConverter;
import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.Dto.Empresa.FormularioRequisicaoDto;
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

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private PromptService promptService;

    @Autowired
    private CriativosService criativosService;

    public Empresa buscaPorId(UUID id){
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        if (empresaOpt.isEmpty()) return null;

        return empresaOpt.get();
    }

    public boolean empresaExistePorRazaoSocialECnpj(String razaoSocial, String cnpj) {
        return empresaRepository.findByRazaoSocialAndCnpj(razaoSocial, cnpj).isPresent();
    }

    public Empresa salvarEmpresa(Empresa novaEmpresa) {
        if (!empresaExistePorRazaoSocialECnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj()))
            return empresaRepository.save(novaEmpresa);
        return null;
    }

    public Empresa atualizarEmpresa(Empresa empresa, EmpresaEditDto empresaAtualizada) {
        if (empresa == null) return null;

        Empresa empresaAtualizadaEntity = EmpresaMapper.atualizaEmpresa(empresaAtualizada, empresa);

        return empresaRepository.save(empresa);
    }

    public FormularioRequisicaoDto cadastrarFormulario(Empresa empresa, FormularioRequisicaoDto formulario) {
        ConfigPrompt configPrompt = new ConfigPrompt();

        String jsonForm = JsonConverter.toJson(formulario);
        configPrompt.setForm(jsonForm);

        if (configRepository.findByEmpresaId(empresa.getId()).isPresent()) return null;

        configPrompt.setEmpresa(empresa);
        configRepository.save(configPrompt);

        salvaPrompt(empresa);
        usuarioService.atualizaStatus(empresa, StatusUsuario.CADASTRO_FINALIZADO);

        return formulario;
    }

    public FormularioRequisicaoDto buscaFormulario(Empresa empresa) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RuntimeException("ConfigPrompt não encontrado"));;

        return JsonConverter.fromJson(configPrompt.getForm());
    }

    public FormularioRequisicaoDto atualizaFormulario(Empresa empresa, FormularioRequisicaoDto formulario) {
        ConfigPrompt configPrompt = configRepository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RuntimeException("ConfigPrompt não encontrado"));;



        String jsonForm = JsonConverter.toJson(formulario);
        configPrompt.setForm(jsonForm);

        configRepository.save(configPrompt);
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
