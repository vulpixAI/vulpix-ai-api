package com.vulpix.api.service;

import com.vulpix.api.config.security.jwt.GerenciadorTokenJwt;
import com.vulpix.api.dto.autenticacao.LoginResponse;
import com.vulpix.api.dto.autenticacao.MfaRequiredResponse;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.repository.UsuarioRepository;
import com.vulpix.api.dto.usuario.UsuarioLoginDto;
import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DisplayName("Teste da Classe UsuarioService")
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Mock
    private EmpresaHelper empresaHelper;
    @Test
    @DisplayName("Dado que um novo usuário é cadastrado, então o usuário deve ser salvo no banco de dados")
    void testCadastrarUsuario() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setSenha("senha123");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(UUID.randomUUID());

        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(novoUsuario)).thenReturn(usuarioSalvo);

        Usuario resultado = usuarioService.cadastrarUsuario(novoUsuario, "123");

        assertNotNull(resultado);
        assertEquals(usuarioSalvo.getId(), resultado.getId());
        verify(usuarioRepository, times(1)).save(novoUsuario);
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    @DisplayName("Dado que o usuário não tenha configurado o MFA, deve retornar MfaRequiredResponse")
    void testAutenticarUsuario_DeveRetornarMfaRequiredResponse_SeUsuarioNaoTemMfa() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("teste@vulpix.com");
        loginDto.setSenha("senha123");
        loginDto.setDispositivoCode("dispositivo123");

        Usuario usuario = new Usuario();
        usuario.setEmail(loginDto.getEmail());
        usuario.setSecretKey(null); // MFA ainda não configurado

        when(usuarioRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(usuario));

        LoginResponse response = usuarioService.autenticarUsuario(loginDto);

        assertNotNull(response);
        assertInstanceOf(MfaRequiredResponse.class, response);
    }
    @Test
    @DisplayName("Dado que um usuário é buscado por email, então o usuário deve ser retornado")
    void testBuscarUsuarioPorEmail() {
        String email = "teste@vulpix.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarUsuarioPorEmail(email);

        assertEquals(email, resultado.getEmail());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Dado que o usuário tenha MFA e o dispositivo seja confiável, deve retornar token JWT")
    void testAutenticarUsuario_TokenRetornado_SeMfaAtivoEConfiavel() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("teste@vulpix.com");
        loginDto.setSenha("senha123");
        loginDto.setDispositivoCode("dispositivo123");

        Usuario usuario = new Usuario();
        usuario.setDispositivoExpiraEm(LocalDateTime.now().plusHours(1));
        usuario.setEmail(loginDto.getEmail());
        usuario.setSecretKey("chave-mfa-existente");
        usuario.setDispositivoConfiavel("dispositivo123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(usuario));
        when(gerenciadorTokenJwt.generateToken(authentication)).thenReturn("tokenJWT");

        LoginResponse response = usuarioService.autenticarUsuario(loginDto);

        assertNotNull(response);
        assertInstanceOf(UsuarioTokenDto.class, response);

        UsuarioTokenDto resultado = (UsuarioTokenDto) response;
        assertEquals("tokenJWT", resultado.getToken());
        assertEquals(usuario.getEmail(), resultado.getEmail());
    }

    @Test
    @DisplayName("Dado que um usuário é buscado por id, então o usuário deve ser deletado")
    void testDeletarUsuario() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.existsById(id)).thenReturn(true);

        boolean resultado = usuarioService.deletarUsuario(id);

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }
}
