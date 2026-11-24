package cl.pagoflex.soporte.aplicacion.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TicketRequest {
    private UUID usuarioId;
    private String usuarioNombre;
    private String tipo;
    private String asunto;
    private String descripcion;
}