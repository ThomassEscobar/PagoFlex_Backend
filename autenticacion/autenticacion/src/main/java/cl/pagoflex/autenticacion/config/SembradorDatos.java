package cl.pagoflex.autenticacion.config;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.pagoflex.autenticacion.dominio.model.RolEntidad;
import cl.pagoflex.autenticacion.dominio.model.UsuarioEntidad;
import cl.pagoflex.autenticacion.infraestructura.persistencia.repository.RepositorioRolJpa;
import cl.pagoflex.autenticacion.infraestructura.persistencia.repository.RepositorioUsuarioJpa;

/**
 * Datos base de PagoFlex.
 *
 * Reparto de cuentas (decisión de proyecto):
 *  - ADMIN    : personal de PagoFlex (@pagoflex.cl). Administran la plataforma,
 *               ven empresas, suspenden organizaciones y atienden los tickets
 *               que envían los ejecutivos. No son clientes: no tienen compromisos.
 *  - EJECUTIVO: personal de una empresa cliente. Gestionan los cobros de su cartera
 *               y envían tickets a PagoFlex.
 *  - USUARIO  : cliente final. Ve y paga sus compromisos y envía tickets a su ejecutivo.
 *
 * Los identificadores son fijos para que coincidan con el microservicio de compromisos
 * y para que las pruebas manuales sean repetibles.
 */
@Configuration
public class SembradorDatos {

    private static final String CLAVE_SEMILLA = "Pagoflex2026!";

    // ---- Roles de aplicación ----
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_EJECUTIVO = "EJECUTIVO";
    public static final String ROL_USUARIO = "USUARIO";

    // ---- Identificadores fijos de usuarios ----
    private static final UUID ANA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID BRUNO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CAROLA_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID JAVIERA_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID RODRIGO_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID THOMAS_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID MATIAS_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID CAMILA_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");

    // ---- Identificadores fijos de empresas (deben coincidir con compromisos) ----
    public static final UUID EMPRESA_INMOBILIARIA_ANDES =
            UUID.fromString("aaaaaaaa-0000-4000-8000-000000000001");
    public static final UUID EMPRESA_CONSTRUCTORA_SUR =
            UUID.fromString("aaaaaaaa-0000-4000-8000-000000000002");
    public static final UUID EMPRESA_CREDITO_DIRECTO =
            UUID.fromString("aaaaaaaa-0000-4000-8000-000000000003");

    @Bean
    CommandLineRunner sembrarUsuarios(RepositorioUsuarioJpa usuarios,
                                      RepositorioRolJpa roles,
                                      PasswordEncoder encoder) {
        return args -> {
            RolEntidad rolUser = roles.findByNombre("USER")
                    .orElseGet(() -> roles.save(new RolEntidad(null, "USER")));
            roles.findByNombre("SERVICE")
                    .orElseGet(() -> roles.save(new RolEntidad(null, "SERVICE")));

            // Administración de la plataforma (PagoFlex)
            crearOActualizar(usuarios, encoder, rolUser, THOMAS_ID,
                    "thomas@pagoflex.cl", "Thomas Escobar", ROL_ADMIN, "13.457.912-4", null);
            crearOActualizar(usuarios, encoder, rolUser, MATIAS_ID,
                    "matias@pagoflex.cl", "Matías Pavez", ROL_ADMIN, "18.902.335-7", null);
            crearOActualizar(usuarios, encoder, rolUser, CAMILA_ID,
                    "camila@pagoflex.cl", "Camila Torres", ROL_ADMIN, "16.774.208-1", null);

            // Ejecutivos de las empresas cliente
            crearOActualizar(usuarios, encoder, rolUser, JAVIERA_ID,
                    "javiera@inmobiliariaandes.cl", "Javiera Soto", ROL_EJECUTIVO,
                    "15.223.876-9", EMPRESA_INMOBILIARIA_ANDES);
            crearOActualizar(usuarios, encoder, rolUser, RODRIGO_ID,
                    "rodrigo@constructorasur.cl", "Rodrigo Díaz", ROL_EJECUTIVO,
                    "12.884.551-2", EMPRESA_CONSTRUCTORA_SUR);

            // Clientes finales (mantienen el UUID de compromisos)
            crearOActualizar(usuarios, encoder, rolUser, ANA_ID,
                    "ana.perez@gmail.com", "Ana Pérez", ROL_USUARIO, "11.111.111-1", null);
            crearOActualizar(usuarios, encoder, rolUser, BRUNO_ID,
                    "bruno.soto@gmail.com", "Bruno Soto", ROL_USUARIO, "14.562.309-8", null);
            crearOActualizar(usuarios, encoder, rolUser, CAROLA_ID,
                    "carola.munoz@gmail.com", "Carola Muñoz", ROL_USUARIO, "19.045.677-3", null);

            System.out.println("✅ Usuarios semilla verificados/creados (3 admin, 2 ejecutivos, 3 clientes).");
        };
    }

    private void crearOActualizar(RepositorioUsuarioJpa usuarios,
                                  PasswordEncoder encoder,
                                  RolEntidad rol,
                                  UUID id,
                                  String correo,
                                  String nombre,
                                  String rolApp,
                                  String rut,
                                  UUID empresaId) {

        UsuarioEntidad existente = usuarios.findByCorreo(correo).orElse(null);

        if (existente == null) {
            UsuarioEntidad u = new UsuarioEntidad();
            u.setId(id);
            u.setCorreo(correo);
            u.setNombreCompleto(nombre);
            u.setHashContrasena(encoder.encode(CLAVE_SEMILLA));
            u.setEstado("ACTIVO");
            u.setRut(rut);
            u.setRol(rolApp);
            u.setEmpresaId(empresaId);
            u.setCreadoEn(Instant.now());
            u.setActualizadoEn(Instant.now());
            u.setRoles(Set.of(rol));
            usuarios.save(u);
            return;
        }

        // Si ya existe, sincronizamos el reparto (rol, RUT y empresa) sin tocar la clave.
        boolean cambio = false;
        if (!rolApp.equals(existente.getRol())) {
            existente.setRol(rolApp);
            cambio = true;
        }
        if (rut != null && !rut.equals(existente.getRut())) {
            existente.setRut(rut);
            cambio = true;
        }
        if (empresaId != null && !empresaId.equals(existente.getEmpresaId())) {
            existente.setEmpresaId(empresaId);
            cambio = true;
        }
        if (!nombre.equals(existente.getNombreCompleto())) {
            existente.setNombreCompleto(nombre);
            cambio = true;
        }
        if (cambio) {
            existente.setActualizadoEn(Instant.now());
            usuarios.save(existente);
        }
    }

    /** Lista de correos sembrados, útil para logs o pruebas. */
    public static List<String> correosSemilla() {
        return List.of(
                "thomas@pagoflex.cl", "matias@pagoflex.cl", "camila@pagoflex.cl",
                "javiera@inmobiliariaandes.cl", "rodrigo@constructorasur.cl",
                "ana.perez@gmail.com", "bruno.soto@gmail.com", "carola.munoz@gmail.com");
    }
}
