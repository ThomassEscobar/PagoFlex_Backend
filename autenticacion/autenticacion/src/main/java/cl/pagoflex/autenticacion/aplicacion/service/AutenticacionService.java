package cl.pagoflex.autenticacion.aplicacion.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.pagoflex.autenticacion.aplicacion.dto.LoginRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.LoginResponse;
import cl.pagoflex.autenticacion.aplicacion.dto.OlvideRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.RegistroRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.UsuarioDto;
import cl.pagoflex.autenticacion.dominio.model.RolEntidad;
import cl.pagoflex.autenticacion.dominio.model.UsuarioEntidad;
import cl.pagoflex.autenticacion.infraestructura.persistencia.repository.RepositorioRolJpa;
import cl.pagoflex.autenticacion.infraestructura.persistencia.repository.RepositorioUsuarioJpa;
import cl.pagoflex.autenticacion.infraestructura.security.JwtEmisor;

@Service
public class AutenticacionService {

    private static final String ROL_USUARIO = "USUARIO";
    private static final String ROL_EJECUTIVO = "EJECUTIVO";

    private final RepositorioUsuarioJpa usuarios;
    private final RepositorioRolJpa roles;
    private final PasswordEncoder encoder;
    private final JwtEmisor jwtEmisor;

    public AutenticacionService(RepositorioUsuarioJpa usuarios,
                                RepositorioRolJpa roles,
                                PasswordEncoder encoder,
                                JwtEmisor jwtEmisor) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.jwtEmisor = jwtEmisor;
    }

    public LoginResponse iniciarSesion(LoginRequest request) {
        UsuarioEntidad usuario = usuarios.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!encoder.matches(request.getContrasena(), usuario.getHashContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("La cuenta está bloqueada. Contacta a PagoFlex.");
        }

        String tokenAcceso = jwtEmisor.generarToken(usuario);
        String tokenRenovacion = UUID.randomUUID().toString();

        return LoginResponse.builder()
                .tokenAcceso(tokenAcceso)
                .tokenRenovacion(tokenRenovacion)
                .expiraEn(Instant.now().plusSeconds(15 * 60).toEpochMilli())
                .usuario(aDto(usuario))
                .build();
    }

    /** Registro público: solo permite crear clientes o ejecutivos, nunca administradores. */
    public void registrar(RegistroRequest request) {
        if (usuarios.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        String rol = request.getRol() == null ? ROL_USUARIO : request.getRol().trim().toUpperCase();
        if (!ROL_USUARIO.equals(rol) && !ROL_EJECUTIVO.equals(rol)) {
            throw new RuntimeException("Rol no permitido en el registro");
        }
        if (ROL_EJECUTIVO.equals(rol) && request.getEmpresaId() == null) {
            throw new RuntimeException("Un ejecutivo debe indicar su empresa");
        }

        UsuarioEntidad u = new UsuarioEntidad();
        u.setId(UUID.randomUUID());
        u.setCorreo(request.getCorreo());
        u.setNombreCompleto(request.getNombreCompleto());
        u.setHashContrasena(encoder.encode(request.getContrasena()));
        u.setEstado("ACTIVO");
        u.setCreadoEn(Instant.now());
        u.setActualizadoEn(Instant.now());
        u.setRut(request.getRut());
        u.setRol(rol);
        u.setEmpresaId(ROL_EJECUTIVO.equals(rol) ? request.getEmpresaId() : null);

        RolEntidad rolUser = roles.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        u.setRoles(Set.of(rolUser));

        usuarios.save(u);
    }

    /**
     * Listado de cuentas para las vistas de administración y de ejecutivo.
     * No devuelve hashes ni datos sensibles.
     */
    public List<UsuarioDto> listarUsuarios() {
        return usuarios.findAll().stream()
                .map(this::aDto)
                .sorted(Comparator.comparing(UsuarioDto::getNombreCompleto,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    public void olvidarContrasena(OlvideRequest request) {
        // Aquí iría la lógica real de envío de correo (MailHog en desarrollo).
        System.out.println("📧 Enviando código de recuperación a: " + request.getCorreo());
    }

    private UsuarioDto aDto(UsuarioEntidad usuario) {
        List<String> rolesSeguridad = usuario.getRoles() == null
                ? List.of()
                : usuario.getRoles().stream().map(RolEntidad::getNombre).collect(Collectors.toList());

        return UsuarioDto.builder()
                .id(usuario.getId())
                .correo(usuario.getCorreo())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .rut(usuario.getRut())
                .empresaId(usuario.getEmpresaId())
                .roles(rolesSeguridad)
                .build();
    }
}
