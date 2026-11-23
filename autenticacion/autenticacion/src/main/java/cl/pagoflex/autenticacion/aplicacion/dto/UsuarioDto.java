package cl.pagoflex.autenticacion.aplicacion.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * Datos del usuario que viajan al cliente.
 *
 * Importante: el campo {@code rol} (ADMIN | EJECUTIVO | USUARIO) es el rol de
 * aplicación que define qué pantallas ve la app Android. El campo {@code roles}
 * sigue existiendo para los roles de seguridad de la API (USER, SERVICE).
 */
@Data
@Builder
public class UsuarioDto {
    private UUID id;
    private String correo;
    private String nombreCompleto;
    private String rol;
    private String rut;
    private UUID empresaId;
    private List<String> roles;
}
