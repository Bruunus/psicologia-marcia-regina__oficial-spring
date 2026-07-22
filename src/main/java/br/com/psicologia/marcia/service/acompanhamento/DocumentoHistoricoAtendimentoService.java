package br.com.psicologia.marcia.service.acompanhamento;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.acompanhamento.DocumentoHistoricoAtendimentoGeradoDTO;
import br.com.psicologia.marcia.DTO.acompanhamento.EmitirHistoricoAtendimentoRequestDTO;
import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import br.com.psicologia.marcia.repository.acompanhamento.AcompanhamentoPacienteRepository;
import br.com.psicologia.marcia.service.documento.BlocoIdentificacaoDocumentoService;
import br.com.psicologia.marcia.service.documento.Cabecalho;
import br.com.psicologia.marcia.service.documento.ConfiguracaoDocumento;
import br.com.psicologia.marcia.service.documento.Rodape;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;

@Service
public class DocumentoHistoricoAtendimentoService {

    private static final String FONTE_TEXTO = "Arial";

    private static final int LINHAS_BASE_ANTES_PRONTUARIO = 13;

    private static final int LINHAS_ASSINATURA = 5;

    private static final int LINHAS_MARGEM_INFERIOR_ASSINATURA = 2;

    private static final int CARACTERES_POR_LINHA_PRONTUARIO = 92;

    private final AcompanhamentoPacienteRepository acompanhamentoPacienteRepository;

    private final PsicologoService psicologoService;

    private final Cabecalho cabecalho;

    private final Rodape rodape;

    private final ConfiguracaoDocumento configuracaoDocumento;

    private final BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService;

    public DocumentoHistoricoAtendimentoService(
            AcompanhamentoPacienteRepository acompanhamentoPacienteRepository,
            PsicologoService psicologoService,
            Cabecalho cabecalho,
            Rodape rodape,
            ConfiguracaoDocumento configuracaoDocumento,
            BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService
    ) {
        this.acompanhamentoPacienteRepository = acompanhamentoPacienteRepository;
        this.psicologoService = psicologoService;
        this.cabecalho = cabecalho;
        this.rodape = rodape;
        this.configuracaoDocumento = configuracaoDocumento;
        this.blocoIdentificacaoDocumentoService = blocoIdentificacaoDocumentoService;
    }

    @Transactional(readOnly = true)
    public DocumentoHistoricoAtendimentoGeradoDTO gerarDocumentoHistorico(EmitirHistoricoAtendimentoRequestDTO dto) {
        List<AcompanhamentoPaciente> acompanhamentos = buscarAcompanhamentosDoPacienteAteDataPresente(
                dto.getPacienteId(),
                dto.getDataAcompanhamento()
        );

        AcompanhamentoPaciente primeiroAcompanhamento = acompanhamentos.get(0);
        Paciente paciente = primeiroAcompanhamento.getPaciente();
        Psicologo psicologo = psicologoService.buscarPsicologoAtivoPrincipal();
        TipoPapelDocumento tipoPapel = configuracaoDocumento.normalizarTipoPapel(dto.getTipoPapel());

        try (
                XWPFDocument documento = new XWPFDocument();
                ByteArrayOutputStream saida = new ByteArrayOutputStream()
        ) {
            configuracaoDocumento.configurar(documento, tipoPapel);
            cabecalho.criar(documento, psicologo, tipoPapel);
            rodape.criar(documento);

            criarEspaco(documento, 2);
            criarTituloIdentificacao(documento);
            blocoIdentificacaoDocumentoService.criarDocx(documento, paciente);
            criarEspaco(documento, 2);
            criarTituloProntuario(documento);
            criarProntuario(documento, acompanhamentos);
            posicionarAssinaturaNoFinalDaUltimaPagina(documento, acompanhamentos, psicologo, tipoPapel);

            documento.write(saida);

            return new DocumentoHistoricoAtendimentoGeradoDTO(
                    saida.toByteArray(),
                    montarNomeArquivo(paciente)
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar documento de histórico de atendimento.", e);
        }
    }

    private List<AcompanhamentoPaciente> buscarAcompanhamentosDoPacienteAteDataPresente(
            Long pacienteId,
            LocalDateTime dataLimite
    ) {
        List<AcompanhamentoPaciente> acompanhamentos = acompanhamentoPacienteRepository
                .findByPacienteIdAndStatusDeleteOrderByDataAcompanhamentoAsc(
                        pacienteId,
                        StatusDelete.NAO_DELETADO
                )
                .stream()
                .filter(acompanhamento -> {
                    if (dataLimite == null || acompanhamento.getDataAcompanhamento() == null) {
                        return true;
                    }

                    return !acompanhamento.getDataAcompanhamento().isAfter(dataLimite);
                })
                .toList();

        if (acompanhamentos.isEmpty()) {
            throw new RuntimeException("Nenhum acompanhamento encontrado para emissão.");
        }

        return acompanhamentos;
    }

    private void criarTituloIdentificacao(XWPFDocument documento) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.CENTER);
        paragrafo.setSpacingBefore(260);
        paragrafo.setSpacingAfter(420);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        run.setBold(true);
        run.setText("Identificação");
    }

    private void criarTituloProntuario(XWPFDocument documento) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.CENTER);
        paragrafo.setSpacingBefore(450);
        paragrafo.setSpacingAfter(300);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        run.setBold(true);
        run.setText("Prontuário");
    }

    private void criarProntuario(XWPFDocument documento, List<AcompanhamentoPaciente> acompanhamentos) {
        int contadorSessao = 1;

        for (AcompanhamentoPaciente acompanhamento : acompanhamentos) {
            criarCabecalhoSessao(documento, acompanhamento, contadorSessao);

            if (!Boolean.TRUE.equals(acompanhamento.getPacienteAusente())) {
                criarTextoSessao(documento, acompanhamento);
            }

            criarEspaco(documento, 1);
            contadorSessao++;
        }
    }

    private void criarCabecalhoSessao(
            XWPFDocument documento,
            AcompanhamentoPaciente acompanhamento,
            int contadorSessao
    ) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.LEFT);
        paragrafo.setSpacingBefore(180);
        paragrafo.setSpacingAfter(120);

        XWPFRun dataRun = paragrafo.createRun();

        aplicarFontePadrao(dataRun);
        dataRun.setBold(true);
        dataRun.setText(formatarDataSessao(acompanhamento));

        XWPFRun sessaoRun = paragrafo.createRun();

        aplicarFontePadrao(sessaoRun);
        sessaoRun.setText(" – " + obterTextoOrdinalSessao(contadorSessao) + " Sessão");

        if (Boolean.TRUE.equals(acompanhamento.getPacienteAusente())) {
            XWPFRun faltaRun = paragrafo.createRun();

            aplicarFontePadrao(faltaRun);
            faltaRun.setText(" Falta do paciente");
        }
    }

    private void criarTextoSessao(XWPFDocument documento, AcompanhamentoPaciente acompanhamento) {
        String textoAcompanhamento = obterTextoAcompanhamento(acompanhamento);

        if (textoAcompanhamento.isBlank()) {
            return;
        }

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.BOTH);
        paragrafo.setIndentationFirstLine(720);
        paragrafo.setSpacingAfter(180);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        adicionarTextoComQuebras(run, textoAcompanhamento);
    }

    private void posicionarAssinaturaNoFinalDaUltimaPagina(
            XWPFDocument documento,
            List<AcompanhamentoPaciente> acompanhamentos,
            Psicologo psicologo,
            TipoPapelDocumento tipoPapel
    ) {
        int linhasPorPagina = configuracaoDocumento.obterLinhasPorPagina(tipoPapel);
        int linhasOcupadas = calcularLinhasOcupadasAteAssinatura(acompanhamentos);
        int restoUltimaPagina = linhasOcupadas % linhasPorPagina;

        if (restoUltimaPagina == 0) {
            restoUltimaPagina = linhasPorPagina;
        }

        int linhasDisponiveis = linhasPorPagina - restoUltimaPagina;
        int linhasNecessariasParaAssinatura = LINHAS_ASSINATURA + LINHAS_MARGEM_INFERIOR_ASSINATURA;

        if (linhasDisponiveis < linhasNecessariasParaAssinatura) {
            criarQuebraDePagina(documento);

            int linhasParaDescerNaNovaPagina = linhasPorPagina - linhasNecessariasParaAssinatura;

            criarEspaco(documento, Math.max(linhasParaDescerNaNovaPagina, 0));
            criarAssinatura(documento, psicologo);
            return;
        }

        int linhasParaDescer = linhasDisponiveis - linhasNecessariasParaAssinatura;

        criarEspaco(documento, Math.max(linhasParaDescer, 0));
        criarAssinatura(documento, psicologo);
    }

    private int calcularLinhasOcupadasAteAssinatura(List<AcompanhamentoPaciente> acompanhamentos) {
        return LINHAS_BASE_ANTES_PRONTUARIO + calcularLinhasProntuario(acompanhamentos);
    }

    private int calcularLinhasProntuario(List<AcompanhamentoPaciente> acompanhamentos) {
        int totalLinhas = 0;

        for (AcompanhamentoPaciente acompanhamento : acompanhamentos) {
            totalLinhas += 2;

            if (!Boolean.TRUE.equals(acompanhamento.getPacienteAusente())) {
                totalLinhas += calcularLinhasTexto(obterTextoAcompanhamento(acompanhamento));
            }

            totalLinhas += 1;
        }

        return totalLinhas;
    }

    private int calcularLinhasTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }

        int totalLinhas = 0;
        String[] linhas = texto.split("\\R", -1);

        for (String linha : linhas) {
            if (linha == null || linha.isBlank()) {
                totalLinhas++;
                continue;
            }

            int tamanho = linha.trim().length();
            int linhasEstimadas = (int) Math.ceil((double) tamanho / CARACTERES_POR_LINHA_PRONTUARIO);

            totalLinhas += Math.max(linhasEstimadas, 1);
        }

        return totalLinhas;
    }

    private void criarQuebraDePagina(XWPFDocument documento) {
        XWPFParagraph paragrafo = documento.createParagraph();
        XWPFRun run = paragrafo.createRun();

        run.addBreak(BreakType.PAGE);
    }

    private void criarAssinatura(XWPFDocument documento, Psicologo psicologo) {
        XWPFParagraph linha = documento.createParagraph();

        linha.setAlignment(ParagraphAlignment.CENTER);
        linha.setSpacingBefore(0);
        linha.setSpacingAfter(80);

        XWPFRun linhaRun = linha.createRun();

        aplicarFontePadrao(linhaRun);
        linhaRun.setText("________________________________________");

        XWPFParagraph nome = documento.createParagraph();

        nome.setAlignment(ParagraphAlignment.CENTER);
        nome.setSpacingAfter(80);

        XWPFRun nomeRun = nome.createRun();

        aplicarFontePadrao(nomeRun);
        nomeRun.setText(valor(psicologo.getNome()));

        XWPFParagraph funcao = documento.createParagraph();

        funcao.setAlignment(ParagraphAlignment.CENTER);
        funcao.setSpacingAfter(80);

        XWPFRun funcaoRun = funcao.createRun();

        aplicarFontePadrao(funcaoRun);
        funcaoRun.setText(valor(psicologo.getFuncaoEmpresa()));

        XWPFParagraph crp = documento.createParagraph();

        crp.setAlignment(ParagraphAlignment.CENTER);
        crp.setSpacingAfter(0);

        XWPFRun crpRun = crp.createRun();

        aplicarFontePadrao(crpRun);
        crpRun.setText("CRP: " + valor(psicologo.getCrp()));
    }

    private void aplicarFontePadrao(XWPFRun run) {
        run.setFontFamily(FONTE_TEXTO);
        run.setFontSize(10);
    }

    private void adicionarTextoComQuebras(XWPFRun run, String texto) {
        String[] linhas = texto.split("\\R", -1);

        for (int i = 0; i < linhas.length; i++) {
            if (i > 0) {
                run.addBreak();
            }

            run.setText(linhas[i]);
        }
    }

    private void criarEspaco(XWPFDocument documento, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            XWPFParagraph paragrafo = documento.createParagraph();

            paragrafo.setSpacingBefore(0);
            paragrafo.setSpacingAfter(0);

            XWPFRun run = paragrafo.createRun();

            run.addBreak();
        }
    }

    private String formatarDataSessao(AcompanhamentoPaciente acompanhamento) {
        if (acompanhamento.getDataAcompanhamento() == null) {
            return "";
        }

        return acompanhamento.getDataAcompanhamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String obterTextoOrdinalSessao(int numero) {
        return switch (numero) {
            case 1 -> "Primeira";
            case 2 -> "Segunda";
            case 3 -> "Terceira";
            case 4 -> "Quarta";
            case 5 -> "Quinta";
            case 6 -> "Sexta";
            case 7 -> "Sétima";
            case 8 -> "Oitava";
            case 9 -> "Nona";
            case 10 -> "Décima";
            case 11 -> "Décima Primeira";
            case 12 -> "Décima Segunda";
            case 13 -> "Décima Terceira";
            case 14 -> "Décima Quarta";
            case 15 -> "Décima Quinta";
            case 16 -> "Décima Sexta";
            case 17 -> "Décima Sétima";
            case 18 -> "Décima Oitava";
            case 19 -> "Décima Nona";
            case 20 -> "Vigésima";
            default -> numero + "ª";
        };
    }

    private String montarNomeArquivo(Paciente paciente) {
        String nomePaciente = "paciente";

        if (paciente != null && paciente.getNomeCompleto() != null && !paciente.getNomeCompleto().isBlank()) {
            nomePaciente = paciente.getNomeCompleto();
        }

        nomePaciente = normalizarNomeArquivo(nomePaciente);

        return "Historico-de-Atendimento-" + nomePaciente + ".docx";
    }

    private String normalizarNomeArquivo(String valor) {
        return valor
                .trim()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", " ");
    }

    private String obterTextoAcompanhamento(AcompanhamentoPaciente acompanhamento) {
        if (acompanhamento.getAcompanhamento() == null) {
            return "";
        }

        return acompanhamento.getAcompanhamento();
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }
}