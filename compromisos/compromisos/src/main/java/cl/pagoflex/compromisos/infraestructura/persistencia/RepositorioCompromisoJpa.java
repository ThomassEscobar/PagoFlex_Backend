package cl.pagoflex.compromisos.infraestructura.persistencia;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.pagoflex.compromisos.dominio.model.CompromisoEntidad;

public interface RepositorioCompromisoJpa extends JpaRepository<CompromisoEntidad, UUID> {

    List<CompromisoEntidad> findByUsuarioIdOrderByFechaVencimientoAsc(UUID usuarioId);

    List<CompromisoEntidad> findByOrganizacion_IdOrderByFechaVencimientoAsc(UUID organizacionId);

    List<CompromisoEntidad> findAllByOrderByFechaVencimientoAsc();
}
