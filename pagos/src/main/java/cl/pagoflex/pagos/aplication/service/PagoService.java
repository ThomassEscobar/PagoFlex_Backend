package cl.pagoflex.pagos.aplication.service;

import cl.pagoflex.pagos.aplication.PagoRequest;
import cl.pagoflex.pagos.aplication.PagoResponse;
import cl.pagoflex.pagos.dominio.model.PagoEntidad;
import cl.pagoflex.pagos.infra.client.ClienteCompromisos;
import cl.pagoflex.pagos.infra.persistencia.RepositorioPagoJpa;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PagoService {

    private final RepositorioPagoJpa repositorio;
    private final ClienteCompromisos clienteCompromisos;

    public PagoService(RepositorioPagoJpa repositorio, ClienteCompromisos clienteCompromisos) {
        this.repositorio = repositorio;
        this.clienteCompromisos = clienteCompromisos;
    }

    @Transactional
    public PagoResponse procesarPago(PagoRequest request, String idempotencyKey, UUID usuarioId) {
        // 1. Verificar idempotencia (evitar doble cobro)
        var pagoExistente = repositorio.findByClaveIdempotencia(idempotencyKey);
        if (pagoExistente.isPresent()) {
            PagoEntidad p = pagoExistente.get();
            return mapearResponse(p);
        }

        // 2. Crear el pago
        PagoEntidad pago = new PagoEntidad();
        pago.setId(UUID.randomUUID());
        pago.setCompromisoId(request.getCompromisoId());
        pago.setUsuarioId(usuarioId);
        pago.setMontoClp(request.getMontoClp());
        pago.setEstado("PAGADO");
        pago.setClaveIdempotencia(idempotencyKey);
        pago.setNumeroComprobante("COMP-" + System.currentTimeMillis());
        pago.setPagadoEn(Instant.now());
        pago.setCreadoEn(Instant.now());

        repositorio.save(pago);

        // 3. Avisar al microservicio de compromisos
        clienteCompromisos.marcarComoPagado(request.getCompromisoId());

        return mapearResponse(pago);
    }

    private PagoResponse mapearResponse(PagoEntidad p) {
        return PagoResponse.builder()
                .id(p.getId())
                .compromisoId(p.getCompromisoId())
                .montoClp(p.getMontoClp())
                .estado(p.getEstado())
                .numeroComprobante(p.getNumeroComprobante())
                .pagadoEn(p.getPagadoEn())
                .build();
    }
}