package com.vulpix.api.Service;


import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Repository.IntegracaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Classe IntegracaoService")
class IntegracaoServiceTest {

    @Mock
    private IntegracaoRepository integracaoRepository;

    @InjectMocks
    private IntegracaoService integracaoService;

    @Test
    @DisplayName("Dado que exista uma integracao com o id informado, quando buscar a integracao pelo id, então deve retornar a integracao")
    void getIntegracaoById_deveRetornarIntegracaoQuandoExiste() {
        UUID id = UUID.randomUUID();
        Integracao integracaoMock = new Integracao();
        integracaoMock.setId(id);

        when(integracaoRepository.findById(id)).thenReturn(Optional.of(integracaoMock));

        Optional<Integracao> resultado = integracaoService.getIntegracaoById(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(integracaoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Dado que não exista uma integracao com o id informado, quando buscar a integracao pelo id, então deve retornar vazio")
    void getIntegracaoById_deveRetornarVazioQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(integracaoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Integracao> resultado = integracaoService.getIntegracaoById(id);

        assertTrue(resultado.isEmpty());
        verify(integracaoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Dado que exista uma integracao com o id informado, quando verificar o access token, então deve retornar a data de expiração")
    void verificarAccessToken_deveRetornarDataQuandoExiste() {
        UUID id = UUID.randomUUID();
        LocalDateTime dataExpiracao = LocalDateTime.now().plusDays(7);
        Integracao integracaoMock = new Integracao();
        integracaoMock.setId(id);
        integracaoMock.setAccessTokenExpireDate(dataExpiracao);

        when(integracaoRepository.findById(id)).thenReturn(Optional.of(integracaoMock));

        Optional<LocalDateTime> resultado = integracaoService.verificarAccessToken(id);

        assertTrue(resultado.isPresent());
        assertEquals(dataExpiracao, resultado.get());
        verify(integracaoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Dado que não exista uma integracao com o id informado, quando verificar o access token, então deve retornar vazio")
    void verificarAccessToken_deveRetornarVazioQuandoNaoExiste() {
        UUID id = UUID.randomUUID();

        when(integracaoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LocalDateTime> resultado = integracaoService.verificarAccessToken(id);

        assertTrue(resultado.isEmpty());
        verify(integracaoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Dado que exista uma integracao com a empresa e tipo informados, quando buscar a integracao, então deve retornar a integracao")
    void save_deveSalvarIntegracao() {
        Integracao integracao = new Integracao();
        when(integracaoRepository.save(integracao)).thenReturn(integracao);

        Integracao resultado = integracaoService.save(integracao);

        assertNotNull(resultado);
        verify(integracaoRepository, times(1)).save(integracao);
    }

    @Test
    @DisplayName("Dado que exista uma integracao com a empresa e tipo informados, quando buscar a integracao, então deve retornar a integracao")
    void deleteById_deveExcluirIntegracaoQuandoExiste() {
        UUID id = UUID.randomUUID();
        when(integracaoRepository.existsById(id)).thenReturn(true);

        integracaoService.deleteById(id);

        verify(integracaoRepository, times(1)).existsById(id);
        verify(integracaoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Dado que não exista uma integracao com o id informado, quando excluir a integracao, então deve lançar exceção")
    void deleteById_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(integracaoRepository.existsById(id)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            integracaoService.deleteById(id);
        });

        assertEquals("Integração não encontrada", exception.getMessage());
        verify(integracaoRepository, times(1)).existsById(id);
        verify(integracaoRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Dado que exista uma integracao com o id informado, quando atualizar a integracao, então deve retornar a integracao atualizada")
    void atualizaIntegracao_deveAtualizarComSucesso() {
        UUID id = UUID.randomUUID();
        Integracao integracaoExistente = new Integracao();
        integracaoExistente.setId(id);
        integracaoExistente.setAccessToken("oldToken");

        Integracao integracaoAtualizada = new Integracao();
        integracaoAtualizada.setAccessToken("newToken");

        when(integracaoRepository.findById(id)).thenReturn(Optional.of(integracaoExistente));
        when(integracaoRepository.save(any(Integracao.class))).thenReturn(integracaoExistente);

        Integracao resultado = integracaoService.atualizaIntegracao(id, integracaoAtualizada);

        assertNotNull(resultado);
        assertEquals("newToken", resultado.getAccessToken());
        verify(integracaoRepository, times(1)).findById(id);
        verify(integracaoRepository, times(1)).save(integracaoExistente);
    }

    @Test
    @DisplayName("Dado que exista uma integracao com o id informado, quando atualizar a integracao sem access token, então deve retornar a integracao atualizada")
    void atualizaIntegracao_deveRetornarNullQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(integracaoRepository.findById(id)).thenReturn(Optional.empty());

        Integracao resultado = integracaoService.atualizaIntegracao(id, new Integracao());

        assertNull(resultado);
        verify(integracaoRepository, times(1)).findById(id);
        verify(integracaoRepository, never()).save(any(Integracao.class));
    }
}

