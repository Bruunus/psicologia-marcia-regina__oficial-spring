package br.com.psicologia.marcia.service.usuario;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.usuario.DefinirSenhaConviteRequest;
import br.com.psicologia.marcia.DTO.usuario.UsuarioConviteRequest;
import br.com.psicologia.marcia.DTO.usuario.UsuarioConviteResponse;
import br.com.psicologia.marcia.DTO.usuario.ValidarTokenConviteResponse;
import br.com.psicologia.marcia.model.ConviteUsuario;
import br.com.psicologia.marcia.model.enums.Role;
import br.com.psicologia.marcia.model.enums.StatusUsuario;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;

@Service
public class UsuarioConviteService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConviteTokenService usuarioConviteTokenService;
    private final UsuarioConviteEmailService usuarioConviteEmailService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioConviteService(
            UsuarioRepository usuarioRepository,
            UsuarioConviteTokenService usuarioConviteTokenService,
            UsuarioConviteEmailService usuarioConviteEmailService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioConviteTokenService = usuarioConviteTokenService;
        this.usuarioConviteEmailService = usuarioConviteEmailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioConviteResponse criarConvite(UsuarioConviteRequest request) {
        String login = normalizarTexto(request.getLogin());
        String email = normalizarEmail(request.getEmail());
        Role role = normalizarRole(request.getRole());
        String cpf = normalizarCpf(request.getCpf());

        Usuario usuario = usuarioRepository.findByLogin(login)
                .map(usuarioExistente -> tratarUsuarioExistente(usuarioExistente, email, cpf))
                .orElseGet(() -> criarUsuarioPendente(login, email, role, cpf));

        ConviteUsuario convite = usuarioConviteTokenService.gerarNovoConvite(usuario);
        String linkConviteDev = usuarioConviteEmailService.enviarConvite(usuario, convite);

        return new UsuarioConviteResponse(
                "convite_enviado_com_sucesso",
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getRole().name(),
                convite.getExpiraEm(),
                linkConviteDev
        );
    }

    @Transactional(readOnly = true)
    public ValidarTokenConviteResponse validarToken(String token) {
        String tokenNormalizado = normalizarTexto(token);

        ConviteUsuario convite = usuarioConviteTokenService.buscarPorToken(tokenNormalizado)
                .orElseThrow(() -> new UsuarioConviteException(HttpStatus.NOT_FOUND, "convite_nao_encontrado"));

        if (!usuarioConviteTokenService.conviteEstaValido(convite)) {
            throw new UsuarioConviteException(HttpStatus.BAD_REQUEST, "convite_expirado_ou_utilizado");
        }

        Usuario usuario = convite.getUsuario();

        if (usuario.getStatusUsuario() != StatusUsuario.PENDENTE_DE_SENHA) {
            throw new UsuarioConviteException(HttpStatus.BAD_REQUEST, "usuario_nao_esta_pendente_de_senha");
        }

        return new ValidarTokenConviteResponse(
                true,
                usuario.getLogin(),
                usuario.getEmail(),
                "convite_valido"
        );
    }

    @Transactional
    public void definirSenha(DefinirSenhaConviteRequest request) {
        String token = normalizarTexto(request.getToken());
        String senha = request.getSenha();

        ConviteUsuario convite = usuarioConviteTokenService.buscarPorTokenComBloqueio(token)
                .orElseThrow(() -> new UsuarioConviteException(HttpStatus.NOT_FOUND, "convite_nao_encontrado"));

        if (!usuarioConviteTokenService.conviteEstaValido(convite)) {
            throw new UsuarioConviteException(HttpStatus.BAD_REQUEST, "convite_expirado_ou_utilizado");
        }

        Usuario usuario = convite.getUsuario();

        if (usuario.getStatusUsuario() != StatusUsuario.PENDENTE_DE_SENHA) {
            throw new UsuarioConviteException(HttpStatus.BAD_REQUEST, "usuario_nao_esta_pendente_de_senha");
        }

        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setStatusUsuario(StatusUsuario.ATIVO);

        usuarioRepository.save(usuario);
        usuarioConviteTokenService.marcarComoUtilizado(convite);
    }

    private Usuario criarUsuarioPendente(String login, String email, Role role, String cpf) {
        Usuario usuario = new Usuario();

        usuario.setLogin(login);
        usuario.setEmail(email);
        usuario.setRole(role);
        usuario.setCpf(passwordEncoder.encode(cpf));
        usuario.setSenha(gerarSenhaTemporariaCriptografada());
        usuario.setStatusUsuario(StatusUsuario.PENDENTE_DE_SENHA);

        return usuarioRepository.save(usuario);
    }

    private Usuario tratarUsuarioExistente(
            Usuario usuario, String emailRecebido, String cpfRecebido) {
        if (usuario.getStatusUsuario() == StatusUsuario.ATIVO) {
            throw new UsuarioConviteException(HttpStatus.CONFLICT, "usuario_ja_ativo");
        }

        if (usuario.getStatusUsuario() == StatusUsuario.INATIVO) {
            throw new UsuarioConviteException(HttpStatus.CONFLICT, "usuario_inativo");
        }

        if (usuario.getEmail() == null || !usuario.getEmail().equalsIgnoreCase(emailRecebido)) {
            throw new UsuarioConviteException(HttpStatus.CONFLICT, "email_nao_confere_com_usuario_pendente");
        }

        if (usuario.getStatusUsuario() != StatusUsuario.PENDENTE_DE_SENHA) {
            throw new UsuarioConviteException(
                    HttpStatus.CONFLICT, "usuario_nao_disponivel_para_convite");
        }

        if (usuario.getCpf() == null
                || !passwordEncoder.matches(cpfRecebido, usuario.getCpf())) {
            throw new UsuarioConviteException(
                    HttpStatus.CONFLICT, "cpf_nao_confere_com_usuario_pendente");
        }

        return usuario;
    }

    private String gerarSenhaTemporariaCriptografada() {
        String senhaTemporaria = UUID.randomUUID().toString();

        return passwordEncoder.encode(senhaTemporaria);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String normalizarCpf(String cpf) {
        String cpfNormalizado = normalizarTexto(cpf);

        if (!cpfNormalizado.matches("[0-9]{11}")) {
            throw new UsuarioConviteException(
                    HttpStatus.BAD_REQUEST, "cpf_deve_conter_11_digitos");
        }

        return cpfNormalizado;
    }

    private Role normalizarRole(String role) {
        String valor = normalizarTexto(role).toUpperCase();

        try {
            return Role.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new UsuarioConviteException(
                    HttpStatus.BAD_REQUEST, "perfil_invalido");
        }
    }

}