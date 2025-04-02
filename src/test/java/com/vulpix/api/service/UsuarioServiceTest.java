package com.vulpix.api.service;

import com.vulpix.api.config.security.jwt.GerenciadorTokenJwt;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.repository.UsuarioRepository;
import com.vulpix.api.dto.usuario.UsuarioLoginDto;
import com.vulpix.api.dto.usuario.UsuarioTokenDto;
import com.vulpix.api.service.usuario.UsuarioService;
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

    @Test
    @DisplayName("Dado que um novo usuário é cadastrado, então o usuário deve ser salvo no banco de dados")
    void testCadastrarUsuario() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setSenha("senha123");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(UUID.randomUUID());

        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(novoUsuario)).thenReturn(usuarioSalvo);

        Usuario resultado = usuarioService.cadastrarUsuario(novoUsuario);

        assertNotNull(resultado);
        assertEquals(usuarioSalvo.getId(), resultado.getId());
        verify(usuarioRepository, times(1)).save(novoUsuario);
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    @DisplayName("Dado que um usuário é buscado por email, então o usuário deve ser retornado")
    void testBuscarUsuarioPorEmail() {
        String email = "teste@vulpix.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorEmail(email);

        assertTrue(resultado.isPresent());
        assertEquals(email, resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Dado que um usuário é buscado por id, então o usuário deve ser retornado")
    void testAutenticarUsuario() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("teste@vulpix.com");
        loginDto.setSenha("senha123");

        Usuario usuario = new Usuario();
        usuario.setEmail(loginDto.getEmail());

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(usuario));
        when(gerenciadorTokenJwt.generateToken(authentication)).thenReturn("tokenJWT");

        UsuarioTokenDto resultado = usuarioService.autenticarUsuario(loginDto);

        assertNotNull(resultado);
        assertEquals("tokenJWT", resultado.getToken());
        assertEquals(usuario.getEmail(), resultado.getEmail());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(gerenciadorTokenJwt, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Dado que um usuário é buscado por id, então o usuário deve ser atualizado")
    void testAtualizarUsuario() {
        UUID id = UUID.randomUUID();
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(id);

        when(usuarioRepository.existsById(id)).thenReturn(true);
        when(usuarioRepository.save(usuarioAtualizado)).thenReturn(usuarioAtualizado);

        Optional<Usuario> resultado = usuarioService.atualizarUsuario(id, usuarioAtualizado);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, times(1)).save(usuarioAtualizado);
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
