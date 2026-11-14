package br.com.psicologia.marcia.service.usuario;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;
import br.com.psicologia.marcia.DTO.usuario.UsuarioRedefinirSenha;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.security.TokenService;
import br.com.psicologia.marcia.security.TokenStore;
import br.com.psicologia.marcia.service.error.MessageError;

@Configuration
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaDeSessaoDeUsuarioService auditoriaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MessageError messageError;

    private static Usuario usuario = new Usuario();

    private AccessUserManagerRecord login;

    /**
     * Motivos que podem ser informados pelo solicitante.
     * A expiração é identificada pelo próprio backend.
     */
    public enum MotivoLogout {
        LOGOUT_MANUAL,
        INATIVIDADE
    }

    public UsuarioService(UsuarioRepository userRepository) {
        this.usuarioRepository = userRepository;
    }

    /**
     * Registra um usuário com CPF e senha criptografados.
     *
     * @return true se o login já existe; false se foi cadastrado.
     */
    public boolean registrarUsuario(Usuario user) {
        if (usuarioRepository.findBylogin(user.getUsername()) != null) {
            return true;
        }

        if (user.getCpf() == null || user.getCpf().length() != 11) {
            throw new IllegalArgumentException(
                    "O CPF inválico para cadastro - verifique o valor."
            );
        }

        String cpfCriptografado = passwordEncoder.encode(user.getCpf());

        user.setCpf(cpfCriptografado);
        user.setSenha(passwordEncoder.encode(user.getPassword()));

        usuarioRepository.save(user);
        return false;
    }

    /**
     * Redefine a senha após conferir o CPF e a confirmação da senha.
     */
    public boolean redefinirSenhaPorCpf(UsuarioRedefinirSenha request) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        Optional<Usuario> optionalUsuario = usuarios.stream()
                .filter(u -> passwordEncoder.matches(
                        request.cpf(),
                        u.getCpf()
                ))
                .findFirst();

        if (optionalUsuario.isEmpty()) {
            return false;
        }

        if (!request.novaSenha().equals(request.confirmacaoSenha())) {
            return false;
        }

        Usuario usuario = optionalUsuario.get();

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        return true;
    }

    /**
     * Carrega os dados utilizados na autenticação do usuário.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        System.out.println("LOGIN RECEBIDO: " + username);

        UsuarioService.usuario.setLogin(username);

        UserDetails findBylogin =
                usuarioRepository.findBylogin(username);

        if (findBylogin == null) {
            System.out.println("Usuário inexistente (UserDetails)");
            messageError.setMessage("usuario_inexiste");

            throw new UsernameNotFoundException(
                    "Entrando em Exception -> "
                    + "HttpRequestMethodNotSupportedException: "
                    + username
            );
        }

        return findBylogin;
    }

    /**
     * Deleta um usuário pelo login e ID.
     */
    public void deletarUsuarioPorLoginEId(String login, Long id) {
        Optional<Usuario> usuarioOptional =
                usuarioRepository.findByLoginAndId(login, id);

        if (usuarioOptional.isPresent()) {
            usuarioRepository.delete(usuarioOptional.get());
        } else {
            throw new NoSuchElementException(
                    "Usuário não encontrado com login: "
                    + login + " e id: " + id
            );
        }
    }

    /**
     * @return true quando o login NÃO está cadastrado.
     */
    public boolean validacaoDeLogin(String login) {
        return !usuarioRepository.existsByLogin(login);
    }

    /**
     * Mantém compatibilidade com chamadas que não informam motivo.
     */
    public String logout(String tokenJWT) {
        return logout(tokenJWT, MotivoLogout.LOGOUT_MANUAL);
    }

    /**
     * Encerra a sessão correspondente ao token recebido.
     *
     * Aceita token expirado apenas para logout, mantendo a validação
     * da assinatura. A auditoria é encerrada pelo ID associado
     * ao token, nunca pela sessão mais recente do usuário.
     *
     * INATIVIDADE é informada pelo frontend.
     * TOKEN_EXPIRADO é determinado pelo backend e tem prioridade.
     */
    public String logout(
            String tokenJWT,
            MotivoLogout motivoSolicitado) {

        final TokenService.DadosTokenLogout dadosToken;

        try {
            dadosToken = tokenService.getDadosParaLogout(tokenJWT);

        } catch (IllegalArgumentException e) {
            return "Token inválido.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro interno ao validar token para logout.";
        }

        /*
         * Usa o mesmo bloqueio do controller de login.
         * Impede a criação de uma nova sessão por esse fluxo
         * enquanto o encerramento atual está sendo processado.
         */
        synchronized (TokenStore.class) {
            TokenStore.SessaoRegistrada sessao =
                    TokenStore.obterSessao(
                            dadosToken.login(),
                            tokenJWT
                    );

            if (sessao == null) {
                /*
                 * O token já não corresponde a uma sessão registrada.
                 * Não remove outro token nem altera outra auditoria.
                 */
                return "Logout efetuado com sucesso. "
                        + "O token informado já não possui sessão ativa.";
            }

            if (sessao.auditoriaId() == null) {
                return "Erro interno: sessão sem vínculo com a auditoria.";
            }

            MotivoLogout motivoRecebido = motivoSolicitado != null
                    ? motivoSolicitado
                    : MotivoLogout.LOGOUT_MANUAL;

            String motivo = dadosToken.expirado()
                    ? "TOKEN_EXPIRADO"
                    : motivoRecebido.name();

            try {
                /*
                 * Persiste o encerramento antes de remover o vínculo
                 * em memória. Se o banco falhar, o vínculo permanece
                 * disponível para uma nova tentativa de logout.
                 *
                 * Token vencido continua bloqueado nas rotas protegidas,
                 * independentemente desta operação.
                 */
                auditoriaService.registrarLogoutDaSessao(
                        sessao.auditoriaId(),
                        dadosToken.login(),
                        motivo
                );

                // Remove exclusivamente o token recebido.
                TokenStore.removerToken(
                        dadosToken.login(),
                        tokenJWT
                );

                return "Logout efetuado com sucesso.";

            } catch (Exception e) {
                e.printStackTrace();
                return "Erro interno ao concluir logout.";
            }
        }
    }

    AccessUserManagerRecord conversorStringParaAccessUserManagerRecord(
            String login) {
        return new AccessUserManagerRecord(login);
    }

    /**
     * Exclui um usuário pelo login e ID.
     */
    public boolean deletarUsuario(String login, Long id) {
        Optional<Usuario> usuarioOptional =
                usuarioRepository.findByIdAndLogin(id, login);

        if (usuarioOptional.isPresent()) {
            usuarioRepository.delete(usuarioOptional.get());
            return true;
        }

        return false;
    }
}