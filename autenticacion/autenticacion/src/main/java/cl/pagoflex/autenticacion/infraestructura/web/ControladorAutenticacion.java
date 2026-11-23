package cl.pagoflex.autenticacion.infraestructura.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.pagoflex.autenticacion.aplicacion.dto.LoginRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.LoginResponse;
import cl.pagoflex.autenticacion.aplicacion.dto.OlvideRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.RegistroRequest;
import cl.pagoflex.autenticacion.aplicacion.dto.UsuarioDto;
import cl.pagoflex.autenticacion.aplicacion.service.AutenticacionService;

@RestController
@RequestMapping("/api/v1/autenticacion")
@CrossOrigin(origins = "*") // Permite que la app Android se conecte
public class ControladorAutenticacion {

    private final AutenticacionService servicioAutenticacion;

    public ControladorAutenticacion(AutenticacionService servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/inicio-sesion")
    public ResponseEntity<LoginResponse> iniciarSesion(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(servicioAutenticacion.iniciarSesion(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<Void> registrar(@RequestBody RegistroRequest request) {
        servicioAutenticacion.registrar(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/contrasena/olvide")
    public ResponseEntity<Void> olvidarContrasena(@RequestBody OlvideRequest request) {
        servicioAutenticacion.olvidarContrasena(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Listado de cuentas para las vistas de administración y de ejecutivo.
     *
     * TODO: cuando el gateway valide el JWT, restringir a rol ADMIN (o a ADMIN y
     * EJECUTIVO) y filtrar por empresa. Hoy queda abierto porque el proyecto aún
     * no tiene filtro de autenticación en la cadena de Spring Security.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        return ResponseEntity.ok(servicioAutenticacion.listarUsuarios());
    }
}
