
package br.com.psicologia.marcia.service.arquivo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.pdf.PdfReader;

import br.com.psicologia.marcia.model.AnexoPaciente;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusPaciente;

import br.com.psicologia.marcia.repository.arquivo.AnexoPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

@Service
public class AnexoPacienteService {

    private static final long TAMANHO_MAXIMO_BYTES =
            10L * 1024L * 1024L;

    /*
     * Pastas correspondentes a estrutura
     * da tela Arquivo.
     *
     * As subpastas possuem identificadores
     * proprios para evitar ambiguidades.
     */
    private static final Set<String> PASTAS_PERMITIDAS =
            Set.of(
                    "QUEIXAS",
                    "RELATORIOS_PSICOLOGICOS",
                    "DECLARACOES_COMPARECIMENTO",
                    "RELATORIOS_ATENDIMENTO",
                    "RECIBOS",
                    "LAUDOS",
                    "OUTROS_ARQUIVOS"
            );

    private final AnexoPacienteRepository anexoPacienteRepository;

    private final ReadPacienteRepository readPacienteRepository;

    private final AnexoPacienteArmazenamentoService armazenamentoService;

    private final TransactionTemplate transactionTemplate;

    public AnexoPacienteService(
            AnexoPacienteRepository anexoPacienteRepository,
            ReadPacienteRepository readPacienteRepository,
            AnexoPacienteArmazenamentoService armazenamentoService,
            PlatformTransactionManager transactionManager
    ) {

        this.anexoPacienteRepository =
                anexoPacienteRepository;

        this.readPacienteRepository =
                readPacienteRepository;

        this.armazenamentoService =
                armazenamentoService;

        this.transactionTemplate =
                new TransactionTemplate(transactionManager);
    }

    /*
     * Valida e persiste um novo documento.
     *
     * O metodo e utilizado pelo endpoint
     * de upload de anexos.
     */
    public AnexoSalvo anexar(
            Long pacienteId,
            String pasta,
            MultipartFile arquivo
    ) {

        validarPacienteId(pacienteId);

        String pastaValidada =
                validarPasta(pasta);

        validarArquivo(arquivo);

        String nomeOriginal =
                validarNomeOriginal(
                        arquivo.getOriginalFilename()
                );

        String formato =
                identificarFormato(nomeOriginal);

        byte[] conteudo =
                lerConteudo(arquivo);

        validarConteudo(
                conteudo,
                formato
        );

        /*
         * Guarda o caminho do arquivo
         * criado durante a transacao.
         *
         * Caso o banco falhe, a limpeza
         * sera executada fora da transacao.
         */
        AtomicReference<String> caminhoCriado =
                new AtomicReference<>();

        try {

            return transactionTemplate.execute(status -> {

                /*
                 * Somente pacientes arquivados
                 * podem receber novos anexos
                 * nesta tela.
                 */
                Paciente paciente =
                        readPacienteRepository
                                .findByIdAndStatusPaciente(
                                        pacienteId,
                                        StatusPaciente.ARQUIVADO
                                )
                                .orElseThrow(
                                        () -> new NoSuchElementException(
                                                "Paciente arquivado não encontrado."
                                        )
                                );

                /*
                 * Salva o documento no
                 * diretorio privado.
                 */
                AnexoPacienteArmazenamentoService.ArquivoArmazenado
                        arquivoArmazenado =
                        armazenamentoService.salvar(
                                pacienteId,
                                conteudo,
                                formato
                        );

                caminhoCriado.set(
                        arquivoArmazenado.caminhoRelativo()
                );

                /*
                 * Cria os metadados.
                 */
                AnexoPaciente anexo =
                        new AnexoPaciente();

                anexo.setPaciente(paciente);

                anexo.setPasta(pastaValidada);

                anexo.setNomeOriginal(nomeOriginal);

                anexo.setNomeArmazenado(
                        arquivoArmazenado.nomeArmazenado()
                );

                anexo.setFormato(
                        formato.toUpperCase(Locale.ROOT)
                );

                anexo.setTipoMime(
                        identificarTipoMime(formato)
                );

                anexo.setTamanhoBytes(
                        (long) conteudo.length
                );

                anexo.setCaminhoRelativo(
                        arquivoArmazenado.caminhoRelativo()
                );

                /*
                 * saveAndFlush antecipa a
                 * execucao do INSERT.
                 *
                 * O TransactionTemplate
                 * tambem aguarda o commit.
                 */
                AnexoPaciente salvo =
                        anexoPacienteRepository
                                .saveAndFlush(anexo);

                return new AnexoSalvo(
                        salvo.getId(),
                        pacienteId,
                        salvo.getPasta(),
                        salvo.getNomeOriginal(),
                        salvo.getFormato(),
                        salvo.getTipoMime(),
                        salvo.getTamanhoBytes(),
                        salvo.getDataAnexo()
                );
            });

        } catch (RuntimeException erro) {

            /*
             * Se a transacao nao foi concluida,
             * tenta remover o arquivo fisico.
             */
            String caminho =
                    caminhoCriado.get();

            if (caminho != null) {

                try {

                    armazenamentoService.excluir(caminho);

                } catch (RuntimeException erroLimpeza) {

                    erro.addSuppressed(erroLimpeza);
                }
            }

            throw erro;
        }
    }

    /*
     * TASK 2.6
     *
     * Lista os anexos de um paciente
     * que esteja arquivado.
     *
     * Retorna somente os metadados
     * necessarios para o frontend.
     *
     * Nao expoe o caminho relativo,
     * o nome UUID ou o arquivo fisico.
     */
    @Transactional(readOnly = true)
    public List<AnexoListado> listarAnexos(
            Long pacienteId
    ) {

        validarPacienteId(pacienteId);

        /*
         * Mantem a regra do modulo Arquivo:
         * o paciente precisa estar arquivado.
         */
        readPacienteRepository
                .findByIdAndStatusPaciente(
                        pacienteId,
                        StatusPaciente.ARQUIVADO
                )
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Paciente arquivado não encontrado."
                        )
                );

        /*
         * Consulta todos os anexos
         * ordenados pelos mais recentes.
         */
        List<AnexoPaciente> anexos =
                anexoPacienteRepository
                        .findByPaciente_IdOrderByDataAnexoDescIdDesc(
                                pacienteId
                        );

        /*
         * Converte as entidades JPA
         * em DTOs seguros para retorno.
         */
        return anexos.stream()
                .map(anexo -> new AnexoListado(
                        anexo.getId(),
                        pacienteId,
                        anexo.getPasta(),
                        anexo.getNomeOriginal(),
                        anexo.getFormato(),
                        anexo.getTipoMime(),
                        anexo.getTamanhoBytes(),
                        anexo.getDataAnexo()
                ))
                .toList();
    }

    /*
     * TASK 2.6
     *
     * Recupera o conteudo fisico de um
     * anexo previamente cadastrado.
     *
     * Exige que o paciente esteja
     * arquivado e que o documento
     * pertença ao paciente informado.
     *
     * O caminho do armazenamento
     * permanece interno ao backend.
     */
    @Transactional(readOnly = true)
    public AnexoRecuperado recuperarAnexo(
            Long pacienteId,
            Long anexoId
    ) {

        validarPacienteId(pacienteId);

        if (anexoId == null || anexoId <= 0) {

            throw new IllegalArgumentException(
                    "Anexo inválido."
            );
        }

        /*
         * Verifica a situacao do paciente.
         */
        readPacienteRepository
                .findByIdAndStatusPaciente(
                        pacienteId,
                        StatusPaciente.ARQUIVADO
                )
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Paciente arquivado não encontrado."
                        )
                );

        /*
         * Busca o anexo verificando
         * simultaneamente o ID do anexo
         * e o ID do paciente.
         *
         * Isso impede recuperar um anexo
         * vinculado a outro paciente
         * apenas informando seu ID.
         */
        AnexoPaciente anexo =
                anexoPacienteRepository
                        .findByIdAndPaciente_Id(
                                anexoId,
                                pacienteId
                        )
                        .orElseThrow(
                                () -> new NoSuchElementException(
                                        "Anexo não encontrado para este paciente."
                                )
                        );

        /*
         * O caminho relativo e utilizado
         * exclusivamente pelo backend.
         *
         * O armazenamentoService.carregar()
         * verifica o caminho e a existencia
         * do arquivo no diretorio privado.
         */
        Resource recurso =
                armazenamentoService.carregar(
                        anexo.getCaminhoRelativo()
                );

        /*
         * Retorna o recurso e os metadados
         * necessarios ao futuro Controller.
         *
         * Nenhum caminho interno do
         * servidor e disponibilizado.
         */
        return new AnexoRecuperado(
                recurso,
                anexo.getNomeOriginal(),
                anexo.getTipoMime(),
                anexo.getTamanhoBytes()
        );
    }

    /*
     * Validacao do identificador do paciente.
     */
    private void validarPacienteId(Long pacienteId) {

        if (
                pacienteId == null
                || pacienteId <= 0
        ) {

            throw new IllegalArgumentException(
                    "Paciente inválido."
            );
        }
    }

    /*
     * Validacao da pasta de destino.
     */
    private String validarPasta(String pasta) {

        if (
                pasta == null
                || pasta.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Pasta de destino não informada."
            );
        }

        String pastaNormalizada =
                pasta.trim().toUpperCase(Locale.ROOT);

        if (!PASTAS_PERMITIDAS.contains(pastaNormalizada)) {

            throw new IllegalArgumentException(
                    "Pasta de destino não permitida."
            );
        }

        return pastaNormalizada;
    }

    /*
     * Validacoes iniciais do arquivo.
     */
    private void validarArquivo(MultipartFile arquivo) {

        if (
                arquivo == null
                || arquivo.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Selecione um arquivo para anexar."
            );
        }

        if (
                arquivo.getSize() <= 0
                || arquivo.getSize() > TAMANHO_MAXIMO_BYTES
        ) {

            throw new IllegalArgumentException(
                    "O arquivo deve possuir no máximo 10 MB."
            );
        }
    }

    /*
     * Normaliza o nome original.
     *
     * O nome e utilizado para exibicao,
     * nunca para criar o caminho fisico.
     */
    private String validarNomeOriginal(String nomeOriginal) {

        if (
                nomeOriginal == null
                || nomeOriginal.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Nome do arquivo não informado."
            );
        }

        String nome =
                nomeOriginal.replace('\\', '/');

        int ultimaBarra =
                nome.lastIndexOf('/');

        if (ultimaBarra >= 0) {

            nome = nome.substring(ultimaBarra + 1);
        }

        nome = nome.trim();

        if (
                nome.isEmpty()
                || nome.length() > 255
        ) {

            throw new IllegalArgumentException(
                    "Nome do arquivo inválido."
            );
        }

        for (int i = 0; i < nome.length(); i++) {

            if (Character.isISOControl(nome.charAt(i))) {

                throw new IllegalArgumentException(
                        "Nome do arquivo contém caracteres inválidos."
                );
            }
        }

        return nome;
    }

    /*
     * Identifica o formato declarado
     * pela extensao do nome.
     *
     * A validacao do conteudo real
     * acontece em seguida.
     */
    private String identificarFormato(String nomeOriginal) {

        String nome =
                nomeOriginal.toLowerCase(Locale.ROOT);

        if (nome.endsWith(".pdf")) {

            return "pdf";
        }

        if (nome.endsWith(".docx")) {

            return "docx";
        }

        throw new IllegalArgumentException(
                "Formato não permitido. Utilize PDF ou DOCX."
        );
    }

    /*
     * Carrega o conteudo respeitando
     * o tamanho maximo permitido.
     */
    private byte[] lerConteudo(MultipartFile arquivo) {

        try {

            byte[] conteudo =
                    arquivo.getBytes();

            if (
                    conteudo.length == 0
                    || conteudo.length > TAMANHO_MAXIMO_BYTES
            ) {

                throw new IllegalArgumentException(
                        "O arquivo deve possuir no máximo 10 MB."
                );
            }

            return conteudo;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Não foi possível ler o arquivo enviado.",
                    e
            );
        }
    }

    /*
     * Verifica se o conteudo corresponde
     * ao formato permitido.
     */
    private void validarConteudo(
            byte[] conteudo,
            String formato
    ) {

        switch (formato) {

            case "pdf":
                validarPdf(conteudo);
                break;

            case "docx":
                validarDocx(conteudo);
                break;

            default:
                throw new IllegalArgumentException(
                        "Formato de documento não permitido."
                );
        }
    }

    /*
     * Validacao do PDF usando OpenPDF.
     *
     * Nao depende apenas da extensao
     * nem do MIME enviado pelo navegador.
     */
    private void validarPdf(byte[] conteudo) {

        if (
                conteudo.length < 5
                || conteudo[0] != '%'
                || conteudo[1] != 'P'
                || conteudo[2] != 'D'
                || conteudo[3] != 'F'
                || conteudo[4] != '-'
        ) {

            throw new IllegalArgumentException(
                    "O conteúdo não corresponde a um PDF válido."
            );
        }

        PdfReader leitor = null;

        try {

            leitor = new PdfReader(conteudo);

            if (leitor.getNumberOfPages() <= 0) {

                throw new IllegalArgumentException(
                    "O PDF não possui páginas válidas."
                );
            }

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Não foi possível validar o documento PDF.",
                    e
            );

        } finally {

            if (leitor != null) {

                leitor.close();
            }
        }
    }

    /*
     * Validacao do DOCX usando Apache POI.
     *
     * O documento precisa ser reconhecido
     * como um arquivo Word válido.
     */
    private void validarDocx(byte[] conteudo) {

        try (
                ByteArrayInputStream entrada =
                        new ByteArrayInputStream(conteudo);

                XWPFDocument documento =
                        new XWPFDocument(entrada)
        ) {

            /*
             * Forca a leitura da estrutura
             * principal do documento.
             */
            documento.getDocument();

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Não foi possível validar o documento DOCX.",
                    e
            );
        }
    }

    /*
     * Define o MIME com base no formato
     * realmente validado.
     */
    private String identificarTipoMime(String formato) {

        if ("pdf".equals(formato)) {

            return "application/pdf";
        }

        if ("docx".equals(formato)) {

            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        throw new IllegalArgumentException(
                "Formato de documento não permitido."
        );
    }

    /*
     * DTO interno de retorno do upload.
     *
     * Nao expoe caminhos fisicos nem
     * relacionamentos JPA ao frontend.
     */
    public record AnexoSalvo(
            Long id,
            Long pacienteId,
            String pasta,
            String nomeOriginal,
            String formato,
            String tipoMime,
            Long tamanhoBytes,
            LocalDateTime dataAnexo
    ) {
    }

    /*
     * TASK 2.6
     *
     * DTO de listagem dos anexos.
     *
     * Utilizado pelo Controller
     * e pela integracao com o Angular.
     */
    public record AnexoListado(
            Long id,
            Long pacienteId,
            String pasta,
            String nomeOriginal,
            String formato,
            String tipoMime,
            Long tamanhoBytes,
            LocalDateTime dataAnexo
    ) {
    }

    /*
     * TASK 2.6
     *
     * Retorno interno utilizado para
     * disponibilizar o arquivo fisico
     * por um endpoint autenticado.
     *
     * O caminho do servidor e o nome
     * UUID nao sao expostos.
     */
    public record AnexoRecuperado(
            Resource recurso,
            String nomeOriginal,
            String tipoMime,
            Long tamanhoBytes
    ) {
    }
}
