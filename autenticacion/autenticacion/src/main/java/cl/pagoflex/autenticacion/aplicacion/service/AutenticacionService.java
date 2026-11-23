package cl.pagoflex.autenticacion.aplicacion.service;

import java.time.Instant;
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

    private final RepositorioUsuarioJpa usuarios;
    private final RepositorioRolJpa roles; // Agregado
    private final PasswordEncoder encoder;
    private final JwtEmisor jwtEmisor;

    // Actualizamos el constructor
    public AutenticacionService(RepositorioUsuarioJpa usuarios, RepositorioRolJpa roles, PasswordEncoder encoder, JwtEmisor jwtEmisor) {
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

        String tokenAcceso = jwtEmisor.generarToken(usuario);
        String tokenRenovacion = UUID.randomUUID().toString(); 

        List<String> listaRoles = usuario.getRoles().stream()
                .map(r -> r.getNombre())
                .collect(Collectors.toList());

        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(usuario.getId())
                .correo(usuario.getCorreo())
                .nombreCompleto(usuario.getNombreCompleto())
                .roles(listaRoles)
                .build();

        return LoginResponse.builder()
                .tokenAcceso(tokenAcceso)
                .tokenRenovacion(tokenRenovacion)
                .expiraEn(Instant.now().plusSeconds(15 * 60).toEpochMilli())
                .usuario(usuarioDto)
                .build();
    }

    // --- MÉTODO DE REGISTRO NUEVO ---
    public void registrar(RegistroRequest request) {
        if (usuarios.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        UsuarioEntidad u = new UsuarioEntidad();
        u.setId(UUID.randomUUID());
        u.setCorreo(request.getCorreo());
        u.setNombreCompleto(request.getNombreCompleto());
        u.setHashContrasena(encoder.encode(request.getContrasena()));
        u.setEstado("ACTIVO");
        u.setCreadoEn(Instant.now());
        u.setActualizadoEn(Instant.now());
        
        // Guardamos los datos extras
        u.setRut(request.getRut());
        u.setRol(request.getRol() != null ? request.getRol() : "USUARIO");
        u.setEmpresaId(request.getEmpresaId());
        
        // Asignamos el rol por defecto
        RolEntidad rolUser = roles.findByNombre("USER").orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        u.setRoles(Set.of(rolUser));
        
        usuarios.save(u);
    }

    // --- MÉTODO PARA OLVIDÉ MI CONTRASEÑA ---
    public void olvidarContrasena(OlvideRequest request) {
        // Aquí iría la lógica real de envío de correo.
        System.out.println("📧 Enviando código a: " + request.getCorreo());
    }
}