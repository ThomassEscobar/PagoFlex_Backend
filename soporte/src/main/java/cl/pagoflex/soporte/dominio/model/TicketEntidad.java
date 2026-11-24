package cl.pagoflex.soporte.dominio.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tickets")
@Data
public class TicketEntidad {
    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "usuario_nombre", nullable = false, length = 120)
    private String usuarioNombre;

    @Column(nullable = false, length = 50)
    private String tipo; // SOPORTE, REGISTRO_EMPRESA, VINCULACION_EMPRESA

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado; // PENDIENTE, APROBADO, RECHAZADO

    @Column(name = "motivo_respuesta", columnDefinition = "TEXT")
    private String motivoRespuesta;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;
}