package cl.pagoflex.soporte.aplicacion.dto;

import lombok.Data;

@Data
public class ResolverTicketRequest {
    private String estado; // APROBADO o RECHAZADO
    private String motivoRespuesta;
}