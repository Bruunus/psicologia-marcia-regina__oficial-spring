
package br.com.psicologia.marcia.service.arquivo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AnexoPacienteArmazenamentoService {

    private final Path diretorioRaiz;

    public AnexoPacienteArmazenamentoService(
        @Value(
            "${PSICOLOGIA_ANEXOS_DIR:${user.home}/psicologia-marcia-dados/anexos}"
        )
        String diretorioConfigurado
    ) {
        this.diretorioRaiz = Path.of(diretorioConfigurado)
            .toAbsolutePath()
            .normalize();
    }

    /*
     * Prepara o diretorio privado durante
     * a inicializacao do Spring Boot.
     */
    @PostConstruct
    public void inicializar() {

        try {
            Files.createDirectories(diretorioRaiz);

            configurarPermissoesDiretorio(diretorioRaiz);

        } catch (IOException e) {

            throw new IllegalStateException(
                "Não foi possível preparar o diretório privado de anexos.",
                e
            );
        }
    }

    /*
     * Armazena o conteudo previamente validado
     * pela camada responsavel pelo upload.
     *
     * A validacao real de PDF/DOCX sera
     * implementada na proxima tarefa.
     */
    public ArquivoArmazenado salvar(
        Long pacienteId,
        byte[] conteudo,
        String formato
    ) {

        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException(
                "Paciente inválido para armazenamento."
            );
        }

        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException(
                "O conteúdo do arquivo está vazio."
            );
        }

        String extensao = validarFormato(formato);

        String nomeArmazenado =
            UUID.randomUUID() + "." + extensao;

        String caminhoRelativo =
            pacienteId + "/" + nomeArmazenado;

        Path destino = resolverCaminho(caminhoRelativo);

        try {

            Path pastaPaciente = destino.getParent();

            Files.createDirectories(pastaPaciente);

            configurarPermissoesDiretorio(pastaPaciente);

            Files.write(
                destino,
                conteudo,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );

            configurarPermissoesArquivo(destino);

            return new ArquivoArmazenado(
                nomeArmazenado,
                caminhoRelativo
            );

        } catch (IOException e) {

            try {
                Files.deleteIfExists(destino);
            } catch (IOException erroLimpeza) {
                e.addSuppressed(erroLimpeza);
            }

            throw new IllegalStateException(
                "Não foi possível armazenar o anexo.",
                e
            );
        }
    }

    /*
     * Recupera um arquivo armazenado.
     *
     * O metodo nao disponibiliza nenhuma
     * URL publica ao navegador.
     */
    public Resource carregar(String caminhoRelativo) {

        Path caminho = resolverCaminho(caminhoRelativo);

        if (
            !Files.isRegularFile(
                caminho,
                LinkOption.NOFOLLOW_LINKS
            )
            || !Files.isReadable(caminho)
        ) {
            throw new IllegalArgumentException(
                "Arquivo anexado não encontrado."
            );
        }

        return new FileSystemResource(caminho);
    }

    /*
     * Exclui um arquivo fisico.
     *
     * A decisao de excluir um anexo
     * cabera a Service de negocio.
     */
    public void excluir(String caminhoRelativo) {

        Path caminho = resolverCaminho(caminhoRelativo);

        try {

            Files.deleteIfExists(caminho);

        } catch (IOException e) {

            throw new IllegalStateException(
                "Não foi possível excluir o arquivo anexado.",
                e
            );
        }
    }

    /*
     * Somente PDF e DOCX sao aceitos
     * como formato declarado.
     *
     * A assinatura e o conteudo real
     * ainda precisarao ser validados
     * antes da chamada ao salvar().
     */
    private String validarFormato(String formato) {

        if (formato == null) {
            throw new IllegalArgumentException(
                "Formato do arquivo não informado."
            );
        }

        String formatoNormalizado =
            formato.trim().toLowerCase(Locale.ROOT);

        if (
            !formatoNormalizado.equals("pdf")
            && !formatoNormalizado.equals("docx")
        ) {
            throw new IllegalArgumentException(
                "Formato não permitido. Utilize PDF ou DOCX."
            );
        }

        return formatoNormalizado;
    }

    /*
     * Impede que caminhos relativos apontem
     * para fora do diretorio privado.
     */
    private Path resolverCaminho(String caminhoRelativo) {

        if (
            caminhoRelativo == null
            || caminhoRelativo.isBlank()
        ) {
            throw new IllegalArgumentException(
                "Caminho do anexo inválido."
            );
        }

        Path caminho = diretorioRaiz
            .resolve(caminhoRelativo)
            .normalize();

        if (
            !caminho.startsWith(diretorioRaiz)
            || caminho.equals(diretorioRaiz)
        ) {
            throw new IllegalArgumentException(
                "Caminho do anexo não permitido."
            );
        }

        /*
         * Rejeita links simbolicos existentes
         * dentro da arvore privada.
         */
        Path atual = diretorioRaiz;

        Path relativo = diretorioRaiz.relativize(caminho);

        for (Path parte : relativo) {

            atual = atual.resolve(parte);

            if (Files.isSymbolicLink(atual)) {
                throw new IllegalArgumentException(
                    "Caminho do anexo não permitido."
                );
            }
        }

        return caminho;
    }

    /*
     * Linux: restringe acesso ao diretorio
     * para o usuario do processo.
     */
    private void configurarPermissoesDiretorio(
        Path diretorio
    ) throws IOException {

        try {

            Files.setPosixFilePermissions(
                diretorio,
                PosixFilePermissions.fromString(
                    "rwx------"
                )
            );

        } catch (UnsupportedOperationException e) {
            // Sistema de arquivos sem suporte POSIX.
        }
    }

    /*
     * Linux: permite leitura e escrita somente
     * ao usuario proprietario.
     */
    private void configurarPermissoesArquivo(
        Path arquivo
    ) throws IOException {

        try {

            Files.setPosixFilePermissions(
                arquivo,
                PosixFilePermissions.fromString(
                    "rw-------"
                )
            );

        } catch (UnsupportedOperationException e) {
            // Sistema de arquivos sem suporte POSIX.
        }
    }

    /*
     * Retorno utilizado pela futura Service
     * responsavel pelo upload e persistencia.
     */
    public record ArquivoArmazenado(
        String nomeArmazenado,
        String caminhoRelativo
    ) {
    }
}
