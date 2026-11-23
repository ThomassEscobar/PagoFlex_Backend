package cl.pagoflex.pagos.infra.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.pagoflex.pagos.dominio.model.PagoEntidad;

public interface RepositorioPagoJpa extends JpaRepository<PagoEntidad, UUID> {
    Optional<PagoEntidad> findByClaveIdempotencia(String claveIdempotencia);
}