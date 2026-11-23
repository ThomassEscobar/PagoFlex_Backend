package cl.pagoflex.compromisos.infraestructura.seguridad;

import java.util.Base64;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lectura del JWT que envía la app en la cabecera Authorization.
 *
 * IMPORTANTE: esta clase NO valida la firma del token; solo lee los datos que
 * necesita el servicio (identidad y rol) para decidir qué compromisos devolver.
 * La validación de firma corresponde al API Gateway (pendiente en el proyecto).
 */
public final class LectorToken {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public record DatosToken(UUID usuarioId, String rol, UUID empresaId) {
    }

    private LectorToken() {
    }

    public static DatosToken leer(String encabezado) {
        if (encabezado == null || encabezado.isBlank()) {
            return null;
        }

        String token = encabezado.startsWith("Bearer ")
                ? encabezado.substring("Bearer ".length()).trim()
                : encabezado.trim();

        String[] partes = token.split("\\.");
        if (partes.length < 2) {
            return null;
        }

        try {
            byte[] payload = Base64.getUrlDecoder().decode(partes[1]);
            JsonNode json = MAPPER.readTree(payload);

            UUID usuarioId = aUuid(json.hasNonNull("sub") ? json.get("sub").asText() : null);
            String rol = json.hasNonNull("rol") ? json.get("rol").asText() : null;
            UUID empresaId = aUuid(json.hasNonNull("empresaId") ? json.get("empresaId").asText() : null);

            return new DatosToken(usuarioId, rol, empresaId);
        } catch (Exception e) {
            return null;
        }
    }

    private static UUID aUuid(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
