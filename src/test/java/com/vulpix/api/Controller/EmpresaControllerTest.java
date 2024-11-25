package com.vulpix.api.Controller;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Dto.Empresa.FormularioRequisicaoDto;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DisplayName("Teste da Clase EmpresaController")
class EmpresaControllerTest {

    @InjectMocks
    private EmpresaController empresaController;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Mock
    private EmpresaHelper empresaHelper;

    private Empresa empresaMock;
    private FormularioRequisicaoDto formularioMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empresaMock = new Empresa();

        formularioMock = new FormularioRequisicaoDto();
        formularioMock.setSlogan("Mocked Slogan");
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa existe, ao atualizar a empresa, então a empresa é atualizada com sucesso")
    void testAtualizarEmpresaComSucesso() {
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn("usuario@mock.com");
        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetailsMock);

        when(empresaHelper.buscarEmpresaPeloUsuario("usuario@mock.com")).thenReturn(empresaMock);

        Empresa empresaAtualizada = new Empresa();
        empresaAtualizada.setRazaoSocial("Empresa Atualizada");
        when(empresaService.atualizarEmpresa(empresaMock, empresaAtualizada)).thenReturn(empresaAtualizada);

        ResponseEntity<Empresa> response = empresaController.atualizar(empresaAtualizada);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Empresa Atualizada", response.getBody().getRazaoSocial());
        verify(empresaService, times(1)).atualizarEmpresa(empresaMock, empresaAtualizada);
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa não existe, ao atualizar a empresa, então a empresa não é encontrada")
    void testAtualizarEmpresaNaoEncontrada() {
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn("usuario@mock.com");
        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetailsMock);

        when(empresaHelper.buscarEmpresaPeloUsuario("usuario@mock.com")).thenReturn(null);

        ResponseEntity<Empresa> response = empresaController.atualizar(empresaMock);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa existe, ao cadastrar um formulário, então o formulário é cadastrado com sucesso")
    void testCadastrarFormularioComSucesso() {
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn("usuario@mock.com");
        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetailsMock);

        when(empresaHelper.buscarEmpresaPeloUsuario("usuario@mock.com")).thenReturn(empresaMock);

        when(empresaService.cadastrarFormulario(empresaMock, formularioMock)).thenReturn(formularioMock);

        ResponseEntity<FormularioRequisicaoDto> response = empresaController.cadastrarFormulario(formularioMock);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Mocked Slogan", response.getBody().getSlogan());
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa não existe, ao cadastrar um formulário, então a empresa não é encontrada")
    void testCadastrarFormularioEmpresaNaoEncontrada() {
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn("usuario@mock.com");
        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetailsMock);

        when(empresaHelper.buscarEmpresaPeloUsuario("usuario@mock.com")).thenReturn(null);

        ResponseEntity<FormularioRequisicaoDto> response = empresaController.cadastrarFormulario(formularioMock);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
