package com.vulpix.api.controller;

import com.vulpix.api.controller.impl.EmpresaControllerImpl;
import com.vulpix.api.dto.empresa.EmpresaEditDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.exception.exceptions.NaoEncontradoException;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.empresa.FormularioRequisicaoDto;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Teste da Classe EmpresaController")
class EmpresaControllerTest {

    @InjectMocks
    private EmpresaControllerImpl empresaController;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Mock
    private EmpresaHelper empresaHelper;
    @Mock
    private Empresa empresaMock;
    @Mock
    private FormularioRequisicaoDto formularioMock;
    @Mock
    private EmpresaEditDto empresaEditDto;

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
        when(empresaService.atualizarEmpresa(empresaMock,empresaEditDto)).thenReturn(empresaEditDto);

        ResponseEntity<EmpresaEditDto> response = empresaController.atualizar(empresaEditDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(empresaService, times(1)).atualizarEmpresa(empresaMock, empresaEditDto);
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa não existe, ao atualizar a empresa, então a empresa não é encontrada")
    void testAtualizarEmpresaNaoEncontrada() {
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn("usuario@mock.com");
        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(userDetailsMock);
        when(empresaHelper.buscarEmpresaPeloUsuario("usuario@mock.com")).thenReturn(null);

        assertThrows(NaoEncontradoException.class, () -> {
            empresaController.atualizar(empresaEditDto);
        });
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

        assertThrows(NaoEncontradoException.class, () -> {
            empresaController.cadastrarFormulario(formularioMock);
        });
    }
}
