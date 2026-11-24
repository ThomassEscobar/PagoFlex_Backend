package cl.pagoflex.soporte.infraestructura.web;

import cl.pagoflex.soporte.aplicacion.dto.ResolverTicketRequest;
import cl.pagoflex.soporte.aplicacion.dto.TicketRequest;
import cl.pagoflex.soporte.aplicacion.service.SoporteService;
import cl.pagoflex.soporte.dominio.model.TicketEntidad;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/soporte/tickets")
@CrossOrigin(origins = "*")
public class ControladorSoporte {

    private final SoporteService soporteService;

    public ControladorSoporte(SoporteService soporteService) {
        this.soporteService = soporteService;
    }

    @PostMapping
    public ResponseEntity<TicketEntidad> crearTicket(@RequestBody TicketRequest request) {
        return ResponseEntity.ok(soporteService.crearTicket(request));
    }

    @GetMapping
    public ResponseEntity<List<TicketEntidad>> listarTickets() {
        return ResponseEntity.ok(soporteService.listarTickets());
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<TicketEntidad> resolverTicket(
            @PathVariable UUID id,
            @RequestBody ResolverTicketRequest request) {
        return ResponseEntity.ok(soporteService.resolverTicket(id, request));
    }
}