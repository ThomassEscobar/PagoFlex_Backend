package cl.pagoflex.pagos.infra.web;

import cl.pagoflex.pagos.aplication.PagoRequest;
import cl.pagoflex.pagos.aplication.PagoResponse;
import cl.pagoflex.pagos.aplication.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@CrossOrigin(origins = "*")
public class ControladorPagos {

    private final PagoService pagoService;

    public ControladorPagos(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> pagar(
            @RequestBody PagoRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        // Si no mandan la key, generamos una (para pruebas)
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            idempotencyKey = UUID.randomUUID().toString();
        }

        // Simulamos que el usuario es Ana
        UUID usuarioId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        PagoResponse response = pagoService.procesarPago(request, idempotencyKey, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}