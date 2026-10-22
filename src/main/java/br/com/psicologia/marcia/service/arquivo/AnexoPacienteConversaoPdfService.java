
package br.com.psicologia.marcia.service.arquivo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnexoPacienteConversaoPdfService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    AnexoPacienteConversaoPdfService.class
            );

    private static final long TEMPO_LIMITE_SEGUNDOS = 60;

    private static final long TAMANHO_MAXIMO_DOCX =
            10L * 1024L * 1024L;

    private static final long TAMANHO_MAXIMO_PDF =
            60L * 1024L * 1024L;

    /**
     * Converte um DOCX em PDF exclusivamente para
     * pre-visualizacao.
     *
     * Nao modifica nem substitui o DOCX original.
     *
     * Retorna os bytes do PDF convertido.
     */
    public byte[] converterDocxParaPdf(byte[] conteudoDocx) {

        if (
                conteudoDocx == null
                || conteudoDocx.length == 0
                || conteudoDocx.length > TAMANHO_MAXIMO_DOCX
        ) {
            throw new IllegalArgumentException(
                    "Conteúdo DOCX inválido para conversão."
            );
        }

        Path diretorioTemporario = null;
        Process processo = null;

        try {

            diretorioTemporario =
                    Files.createTempDirectory(
                            "psicologia-marcia-docx-preview-"
                    );

            Path arquivoDocx =
                    diretorioTemporario.resolve(
                            "documento.docx"
                    );

            Path arquivoPdf =
                    diretorioTemporario.resolve(
                            "documento.pdf"
                    );

            Path diretorioPerfil =
                    diretorioTemporario.resolve(
                            "perfil-libreoffice"
                    );

            Path arquivoLog =
                    diretorioTemporario.resolve(
                            "conversao.log"
                    );

            Files.write(
                    arquivoDocx,
                    conteudoDocx
            );

            /*
             * Cada conversao utiliza um perfil proprio
             * do LibreOffice, evitando conflitos entre
             * requisicoes simultaneas.
             */
            String argumentoPerfil =
                    "-env:UserInstallation="
                            + diretorioPerfil.toUri();

            ProcessBuilder comando =
                    new ProcessBuilder(
                            "libreoffice",
                            argumentoPerfil,
                            "--headless",
                            "--nologo",
                            "--nodefault",
                            "--norestore",
                            "--convert-to",
                            "pdf:writer_pdf_Export",
                            "--outdir",
                            diretorioTemporario.toString(),
                            arquivoDocx.toString()
                    );

            /*
             * Evita bloqueio do processo por excesso
             * de mensagens na saida padrao.
             */
            comando.redirectErrorStream(true);
            comando.redirectOutput(
                    arquivoLog.toFile()
            );

            processo = comando.start();

            boolean terminou =
                    processo.waitFor(
                            TEMPO_LIMITE_SEGUNDOS,
                            TimeUnit.SECONDS
                    );

            if (!terminou) {

                processo.destroyForcibly();
                processo.waitFor();

                throw new IllegalStateException(
                        "Tempo limite excedido na conversão do DOCX."
                );
            }

            if (processo.exitValue() != 0) {

                throw new IllegalStateException(
                        "O LibreOffice não conseguiu converter o DOCX."
                );
            }

            if (!Files.isRegularFile(arquivoPdf)) {

                throw new IllegalStateException(
                        "O arquivo PDF convertido não foi gerado."
                );
            }

            long tamanhoPdf =
                    Files.size(arquivoPdf);

            if (
                    tamanhoPdf == 0
                    || tamanhoPdf > TAMANHO_MAXIMO_PDF
            ) {

                throw new IllegalStateException(
                        "O PDF convertido possui tamanho inválido."
                );
            }

            byte[] conteudoPdf =
                    Files.readAllBytes(arquivoPdf);

            /*
             * Confirma a assinatura basica do PDF
             * antes de retornar ao Controller.
             */
            if (
                    conteudoPdf.length < 5
                    || conteudoPdf[0] != '%'
                    || conteudoPdf[1] != 'P'
                    || conteudoPdf[2] != 'D'
                    || conteudoPdf[3] != 'F'
                    || conteudoPdf[4] != '-'
            ) {

                throw new IllegalStateException(
                        "A conversão não produziu um PDF válido."
                );
            }

            return conteudoPdf;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            if (processo != null && processo.isAlive()) {
                processo.destroyForcibly();
            }

            throw new IllegalStateException(
                    "A conversão do DOCX foi interrompida.",
                    e
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Falha ao executar a conversão DOCX para PDF.",
                    e
            );

        } finally {

            /*
             * Os arquivos temporarios sao removidos
             * independentemente do resultado.
             */
            if (diretorioTemporario != null) {
                limparDiretorioTemporario(
                        diretorioTemporario
                );
            }
        }
    }

    private void limparDiretorioTemporario(
            Path diretorio
    ) {

        try (
                Stream<Path> caminhos =
                        Files.walk(diretorio)
        ) {

            caminhos
                    .sorted(Comparator.reverseOrder())
                    .forEach(caminho -> {

                        try {

                            Files.deleteIfExists(
                                    caminho
                            );

                        } catch (IOException e) {

                            throw new UncheckedIOException(e);
                        }
                    });

        } catch (
                IOException
                | UncheckedIOException e
        ) {

            LOGGER.warn(
                    "Não foi possível limpar integralmente "
                            + "o diretório temporário da conversão.",
                    e
            );
        }
    }
}
