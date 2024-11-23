package com.vulpix.api.Service;

import com.vulpix.api.Entity.ConfigPrompt;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.ConfigRepository;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Service.Integracoes.AgentAi.CriativosService;
import com.vulpix.api.Service.Integracoes.AgentAi.PromptService;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.Utils.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DisplayName("Teste da Classe EmpresaService")
class EmpresaServiceTest {
    @InjectMocks
    private EmpresaService empresaService;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ConfigRepository configRepository;
    @Mock
    private PromptService promptService;
    @Mock
    private CriativosService criativosService;
    @Mock
    private JsonConverter jsonConverter;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("Dado um id de empresa, então deve retornar a empresa")
    void testBuscaPorId_QuandoEmpresaExiste() {
        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(id);

        when(empresaRepository.findById(id)).thenReturn(Optional.of(empresa));

        Empresa resultado = empresaService.buscaPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(empresaRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Dado um id de empresa que não existe, então deve retornar null")
    void testBuscaPorId_QuandoEmpresaNaoExiste() {
        UUID id = UUID.randomUUID();

        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        Empresa resultado = empresaService.buscaPorId(id);

        assertNull(resultado);
        verify(empresaRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Dado uma razão social e um CNPJ, então deve retornar se a empresa existe")
    void testSalvarEmpresa_NovaEmpresa() {
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setRazaoSocial("Vulpix");
        novaEmpresa.setCnpj("123456789");

        when(empresaRepository.findByRazaoSocialAndCnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj()))
                .thenReturn(Optional.empty());
        when(empresaRepository.save(novaEmpresa)).thenReturn(novaEmpresa);

        Empresa resultado = empresaService.salvarEmpresa(novaEmpresa);

        assertNotNull(resultado);
        assertEquals("Vulpix", resultado.getRazaoSocial());
        verify(empresaRepository, times(1)).save(novaEmpresa);
    }

    @Test
    @DisplayName("Dado uma razão social e um CNPJ, então deve retornar null se a empresa já existe")
    void testSalvarEmpresa_EmpresaJaExistente() {
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setRazaoSocial("Vulpix");
        novaEmpresa.setCnpj("123456789");

        when(empresaRepository.findByRazaoSocialAndCnpj(novaEmpresa.getRazaoSocial(), novaEmpresa.getCnpj()))
                .thenReturn(Optional.of(novaEmpresa));

        Empresa resultado = empresaService.salvarEmpresa(novaEmpresa);

        assertNull(resultado);
        verify(empresaRepository, never()).save(novaEmpresa);
    }
    @Test
    @DisplayName("Dado uma empresa e um prompt, então deve retornar os criativos gerados")
    void testBuscaCriativos() {
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        ConfigPrompt configPrompt = new ConfigPrompt();
        configPrompt.setPrompt("Prompt de teste");

        when(configRepository.findByEmpresaId(empresa.getId())).thenReturn(Optional.of(configPrompt));
        when(criativosService.buscaCriativos("Prompt de teste", "userRequest"))
                .thenReturn(new PublicacaoGeradaRetorno());

        PublicacaoGeradaRetorno resultado = empresaService.buscaCriativos(empresa, "userRequest");

        assertNotNull(resultado);
        verify(criativosService, times(1)).buscaCriativos("Prompt de teste", "userRequest");
    }
}