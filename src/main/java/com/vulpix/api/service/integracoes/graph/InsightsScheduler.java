package com.vulpix.api.service.integracoes.graph;

import com.vulpix.api.entity.Integracao;
import com.vulpix.api.repository.IntegracaoRepository;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsightsScheduler {

    @Autowired
    private IntegracaoRepository integracaoRepository;

    @Autowired
    private InsightService insightService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;

    @Scheduled(cron = "0 */5 * * * *")
    public void buscarInsights() {
        List<Integracao> integracoes = integracaoRepository.findByStatusAndTipo(true, TipoIntegracao.INSTAGRAM);

        for (Integracao integracao : integracoes) {
            try {
                insightService.fetchInsightsBatch(integracao);
                System.out.println("Insights atualizados para a integração: " + integracao.getId());
            } catch (Exception e) {
                System.err.println("Erro ao processar integração ID: " + integracao.getId());
                e.printStackTrace();
            }
        }
    }
}
