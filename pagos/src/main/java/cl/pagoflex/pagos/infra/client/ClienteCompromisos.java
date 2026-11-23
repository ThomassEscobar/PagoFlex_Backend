package cl.pagoflex.pagos.infra.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
public class ClienteCompromisos {
    private final RestClient restClient;

    public ClienteCompromisos() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public void marcarComoPagado(UUID compromisoId) {
        try {
            restClient.patch()
                    .uri("/interno/v1/compromisos/{id}/estado", compromisoId)
                    .body(Map.of("estado", "PAGADO"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Error al actualizar compromiso: " + e.getMessage());
            // En producción esto debería manejarse con eventos o colas (Kafka/Rabbit).
        }
    }
}