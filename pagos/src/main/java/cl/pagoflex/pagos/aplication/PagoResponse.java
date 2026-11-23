package cl.pagoflex.pagos.aplication;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PagoResponse {
    private UUID id;
    private UUID compromisoId;
    private Long montoClp;
    private String estado;
    private String numeroComprobante;
    private Instant pagadoEn;
}