package cl.pagoflex.pagos.dominio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Data
public class PagoEntidad {
    @Id
    private UUID id;

    @Column(name = "compromiso_id", nullable = false)
    private UUID compromisoId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "monto_clp", nullable = false)
    private Long montoClp;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "clave_idempotencia", nullable = false, unique = true, length = 80)
    private String claveIdempotencia;

    @Column(name = "numero_comprobante", nullable = false, unique = true, length = 30)
    private String numeroComprobante;

    @Column(name = "pagado_en", nullable = false)
    private Instant pagadoEn;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;
}