package com.vulpix.api.controller;

import com.vulpix.api.controller.impl.IntegracaoControllerImpl;
import com.vulpix.api.dto.integracao.IntegracaoDto;
import com.vulpix.api.dto.integracao.IntegracaoMapper;
import com.vulpix.api.dto.integracao.IntegracaoUpdateDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.IntegracaoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class IntegracaoControllerTest {
    @InjectMocks
    private IntegracaoControllerImpl integracaoController;

    @Mock
    private IntegracaoService integracaoService;

    @Mock
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Mock
    private EmpresaHelper empresaHelper;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Habilitar integração com sucesso")
    void testHabilitarComSucesso() {
        String email = "teste@vulpix.com";
        Empresa empresa = new Empresa();
        IntegracaoDto novaIntegracao = new IntegracaoDto();
        Integracao integracaoSalva = new Integracao();

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(empresaHelper.buscarEmpresaPeloUsuario(email)).thenReturn(empresa);

        try (MockedStatic<IntegracaoMapper> mapperMock = mockStatic(IntegracaoMapper.class)) {
            mapperMock.when(() -> IntegracaoMapper.criaEntidadeIntegracao(novaIntegracao, empresa))
                    .thenReturn(integracaoSalva);

            when(integracaoService.cadastrarIntegracao(integracaoSalva, empresa))
                    .thenReturn(integracaoSalva);

            ResponseEntity<Integracao> response = integracaoController.habilitar(novaIntegracao);

            assertEquals(201, response.getStatusCodeValue());
            assertEquals(integracaoSalva, response.getBody());
        }
    }

    @Test
    @DisplayName("Atualizar integração com sucesso")
    void testAtualizarComSuccess() {
        String email = "teste@vulpix.com";
        Empresa empresa = new Empresa();
        IntegracaoUpdateDto integracaoAtualizada = IntegracaoUpdateDto.builder()
                .accessToken("token")
                .clientId("clientId")
                .clientSecret("clientSecret")
                .igUserId("igUserId")
                .build();
        Integracao integracaoExistente = new Integracao();
        Integracao integracaoAtualizadaSalva = new Integracao();

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(empresaHelper.buscarEmpresaPeloUsuario(email)).thenReturn(empresa);
        when(integracaoService.buscaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM)).thenReturn(integracaoExistente);

        try (MockedStatic<IntegracaoMapper> mapperMock = mockStatic(IntegracaoMapper.class)) {
            mapperMock.when(() -> IntegracaoMapper.criaEntidadeAtualizada(empresa, integracaoAtualizada))
                    .thenReturn(integracaoAtualizadaSalva);

            when(integracaoService.atualizaIntegracao(integracaoExistente.getId(), integracaoAtualizadaSalva))
                    .thenReturn(integracaoAtualizadaSalva);

            ResponseEntity<Integracao> response = integracaoController.atualizar(integracaoAtualizada);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(integracaoAtualizadaSalva, response.getBody());
        }
    }

    @Test
    @DisplayName("Deleter integração com sucesso")
    void testDeletarComSucesso() {
        String email = "teste@vulpix.com";
        Empresa empresa = new Empresa();
        Integracao integracaoExistente = new Integracao();

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(empresaHelper.buscarEmpresaPeloUsuario(email)).thenReturn(empresa);
        when(integracaoService.buscaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM)).thenReturn(integracaoExistente);

        doNothing().when(integracaoService).excluirIntegracao(integracaoExistente.getId());

        ResponseEntity<Void> response = integracaoController.deletar();

        assertEquals(204, response.getStatusCodeValue());
        verify(integracaoService, times(1)).excluirIntegracao(integracaoExistente.getId());
    }

    @Test
    @DisplayName("Verifica se empresa possui integração")
    void testEmpresaPossuiIntegracao() {
        String email = "teste@vulpix.com";
        Empresa empresa = new Empresa();

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(empresaHelper.buscarEmpresaPeloUsuario(email)).thenReturn(empresa);
        when(integracaoService.verificaExistenciaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM))
                .thenReturn(true);

        boolean resultado = integracaoController.possuiIntegracao();

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Retorna dados da integração com sucesso")
    void testRetornoIntegracao() {
        String email = "teste@vulpix.com";
        Empresa empresa = new Empresa();
        Integracao integracao = new Integracao();

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(empresaHelper.buscarEmpresaPeloUsuario(email)).thenReturn(empresa);
        when(integracaoService.retornaIntegracao(empresa)).thenReturn(integracao);

        ResponseEntity<Integracao> response = integracaoController.retornaIntegracao();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(integracao, response.getBody());
    }
}
