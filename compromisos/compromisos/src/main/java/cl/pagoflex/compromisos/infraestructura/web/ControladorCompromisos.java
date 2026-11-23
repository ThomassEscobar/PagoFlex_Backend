package cl.pagoflex.compromisos.infraestructura.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.pagoflex.compromisos.dominio.model.CompromisoEntidad;
import cl.pagoflex.compromisos.infraestructura.persistencia.RepositorioCompromisoJpa;
import cl.pagoflex.compromisos.infraestructura.seguridad.LectorToken;

/**
 * Compromisos por usuario/empresa.
 *
 * Antes este controlador devolvía siempre los compromisos de Ana (UUID fijo), lo
 * que hacía que cualquier cuenta viera los pagos de ella. Ahora se resuelve la
 * identidad desde el JWT que envía la app:
 *
 *  - ADMIN    : todos los compromisos de la plataforma.
 *  - EJECUTIVO: los compromisos de las organizaciones de su empresa.
 *  - USUARIO  : solo los compromisos propios.
 *
 * Sin cabecera Authorization se mantienen los parámetros de consulta para pruebas
 * manuales (por ejemplo, desde PowerShell o Swagger).
 */
@RestController
@RequestMapping("/api/v1/compromisos")
@CrossOrigin(origins = "*")
public class ControladorCompromisos {

    private final RepositorioCompromisoJpa repositorio;

    public ControladorCompromisos(RepositorioCompromisoJpa repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @RequestParam(value = "usuarioId", required = false) UUID usuarioId,
            @RequestParam(value = "organizacionId", required = false) UUID organizacionId) {

        LectorToken.DatosToken datos = LectorToken.leer(autorizacion);

        if (datos != null) {
            String rol = datos.rol() == null ? "USUARIO" : datos.rol().toUpperCase();

            switch (rol) {
                case "ADMIN":
                    return ResponseEntity.ok(repositorio.findAllByOrderByFechaVencimientoAsc());

                case "EJECUTIVO":
                    if (datos.empresaId() == null) {
                        return ResponseEntity.ok(List.<CompromisoEntidad>of());
                    }
                    return ResponseEntity.ok(
                            repositorio.findByOrganizacion_IdOrderByFechaVencimientoAsc(datos.empresaId()));

                default:
                    if (datos.usuarioId() == null) {
                        return ResponseEntity.ok(List.<CompromisoEntidad>of());
                    }
                    return ResponseEntity.ok(
                            repositorio.findByUsuarioIdOrderByFechaVencimientoAsc(datos.usuarioId()));
            }
        }

        // Modo pruebas sin token
        if (usuarioId != null) {
            return ResponseEntity.ok(repositorio.findByUsuarioIdOrderByFechaVencimientoAsc(usuarioId));
        }
        if (organizacionId != null) {
            return ResponseEntity.ok(
                    repositorio.findByOrganizacion_IdOrderByFechaVencimientoAsc(organizacionId));
        }

        return ResponseEntity.status(401)
                .body(Map.of("mensaje", "Falta el token de sesión (Authorization: Bearer ...)"));
    }
}
