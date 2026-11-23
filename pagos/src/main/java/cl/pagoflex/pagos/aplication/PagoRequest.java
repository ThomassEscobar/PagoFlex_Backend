package cl.pagoflex.pagos.aplication;

import lombok.Data;
import java.util.UUID;

@Data
public class PagoRequest {
    private UUID compromisoId;
    private Long montoClp;
}