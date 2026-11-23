package cl.pagoflex.compromisos.config;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.pagoflex.compromisos.dominio.model.CompromisoEntidad;
import cl.pagoflex.compromisos.dominio.model.OrganizacionEntidad;
import cl.pagoflex.compromisos.infraestructura.persistencia.RepositorioCompromisoJpa;
import cl.pagoflex.compromisos.infraestructura.persistencia.RepositorioOrganizacionJpa;

/**
 * Datos base de compromisos.
 *
 * Reglas del reparto:
 *  - Las organizaciones tienen UUID fijo para que el servicio de autenticación
 *    pueda asignar ejecutivos a la misma empresa (empresaId del usuario).
 *  - Ana es una clienta de Inmobiliaria Andes (no es personal de PagoFlex):
 *    conserva sus tres compromisos de siempre.
 *  - Bruno y Carola también son clientes, con movimientos para que las vistas de
 *    ejecutivo y de administración tengan datos reales que mostrar.
 */
@Configuration
public class SembradorDatos {

    // Usuarios (deben coincidir con el sembrador de autenticación)
    private static final UUID ANA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID BRUNO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CAROLA_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    // Empresas (deben coincidir con el sembrador de autenticación)
    private static final UUID INMOBILIARIA_ANDES_ID =
            UUID.fromString("aaaaaaaa-0000-4000-8000-000000000001");
    private static final UUID CONSTRUCTORA_SUR_ID =
            UUID.fromString("aaaaaaaa-0000-4000-8000-000000000002");

    @Bean
    CommandLineRunner sembrarCompromisos(RepositorioCompromisoJpa compromisosRepo,
                                         RepositorioOrganizacionJpa orgRepo) {
        return args -> {
            if (compromisosRepo.count() > 0) return;

            OrganizacionEntidad inmobiliaria = crearOrganizacion(
                    orgRepo, INMOBILIARIA_ANDES_ID, "Inmobiliaria Andes", "76.123.456-7", "INMOBILIARIA");
            OrganizacionEntidad constructora = crearOrganizacion(
                    orgRepo, CONSTRUCTORA_SUR_ID, "Constructora Sur", "77.456.789-0", "CONSTRUCTORA");

            // Ana Pérez — clienta de Inmobiliaria Andes (mantiene sus 3 compromisos)
            crearCompromiso(compromisosRepo, ANA_ID, inmobiliaria, "Arriendo Marzo", 350000L,
                    LocalDate.now().plusDays(5), "PENDIENTE");
            crearCompromiso(compromisosRepo, ANA_ID, inmobiliaria, "Gastos Comunes", 45000L,
                    LocalDate.now().minusDays(2), "VENCIDO");
            crearCompromiso(compromisosRepo, ANA_ID, inmobiliaria, "Arriendo Febrero", 350000L,
                    LocalDate.now().minusMonths(1), "PAGADO");

            // Bruno Soto — cliente de Inmobiliaria Andes
            crearCompromiso(compromisosRepo, BRUNO_ID, inmobiliaria, "Arriendo Marzo", 320000L,
                    LocalDate.now().plusDays(9), "PENDIENTE");
            crearCompromiso(compromisosRepo, BRUNO_ID, inmobiliaria, "Gastos Comunes", 38000L,
                    LocalDate.now().minusDays(5), "VENCIDO");

            // Carola Muñoz — clienta de Constructora Sur
            crearCompromiso(compromisosRepo, CAROLA_ID, constructora, "Cuota Crédito Hipotecario", 480000L,
                    LocalDate.now().plusDays(12), "PENDIENTE");
            crearCompromiso(compromisosRepo, CAROLA_ID, constructora, "Seguro de Desgravamen", 22000L,
                    LocalDate.now().minusMonths(1), "PAGADO");

            System.out.println("✅ Compromisos semilla creados (2 empresas, 3 clientes).");
        };
    }

    private OrganizacionEntidad crearOrganizacion(RepositorioOrganizacionJpa repo,
                                                  UUID id,
                                                  String nombre,
                                                  String rut,
                                                  String tipo) {
        OrganizacionEntidad org = new OrganizacionEntidad();
        org.setId(id);
        org.setNombre(nombre);
        org.setRut(rut);
        org.setTipo(tipo);
        return repo.save(org);
    }

    private void crearCompromiso(RepositorioCompromisoJpa repo,
                                 UUID usuarioId,
                                 OrganizacionEntidad org,
                                 String descripcion,
                                 Long monto,
                                 LocalDate fecha,
                                 String estado) {
        CompromisoEntidad c = new CompromisoEntidad();
        c.setId(UUID.randomUUID());
        c.setUsuarioId(usuarioId);
        c.setOrganizacion(org);
        c.setDescripcion(descripcion);
        c.setMontoClp(monto);
        c.setFechaVencimiento(fecha);
        c.setEstado(estado);
        c.setCreadoEn(Instant.now());
        c.setActualizadoEn(Instant.now());
        repo.save(c);
    }
}
