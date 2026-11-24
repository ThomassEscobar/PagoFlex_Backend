package cl.pagoflex.soporte.aplicacion.service;

import cl.pagoflex.soporte.aplicacion.dto.ResolverTicketRequest;
import cl.pagoflex.soporte.aplicacion.dto.TicketRequest;
import cl.pagoflex.soporte.dominio.model.TicketEntidad;
import cl.pagoflex.soporte.infraestructura.persistencia.RepositorioTicketJpa;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SoporteService {

    private final RepositorioTicketJpa repositorio;

    public SoporteService(RepositorioTicketJpa repositorio) {
        this.repositorio = repositorio;
    }

    public TicketEntidad crearTicket(TicketRequest request) {
        TicketEntidad ticket = new TicketEntidad();
        ticket.setId(UUID.randomUUID());
        ticket.setUsuarioId(request.getUsuarioId());
        ticket.setUsuarioNombre(request.getUsuarioNombre());
        ticket.setTipo(request.getTipo());
        ticket.setAsunto(request.getAsunto());
        ticket.setDescripcion(request.getDescripcion());
        ticket.setEstado("PENDIENTE");
        ticket.setCreadoEn(Instant.now());
        return repositorio.save(ticket);
    }

    public List<TicketEntidad> listarTickets() {
        return repositorio.findAllByOrderByCreadoEnDesc();
    }

    public TicketEntidad resolverTicket(UUID id, ResolverTicketRequest request) {
        TicketEntidad ticket = repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        
        ticket.setEstado(request.getEstado());
        ticket.setMotivoRespuesta(request.getMotivoRespuesta());
        
        // Aquí en un futuro iría la lógica para crear la empresa en la BD real
        // si el ticket es de tipo "REGISTRO_EMPRESA" y se aprueba.
        
        return repositorio.save(ticket);
    }
}