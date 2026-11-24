package cl.pagoflex.soporte.infraestructura.persistencia;

import cl.pagoflex.soporte.dominio.model.TicketEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;


public interface RepositorioTicketJpa extends JpaRepository<TicketEntidad, UUID> {
    List<TicketEntidad> findAllByOrderByCreadoEnDesc();
}