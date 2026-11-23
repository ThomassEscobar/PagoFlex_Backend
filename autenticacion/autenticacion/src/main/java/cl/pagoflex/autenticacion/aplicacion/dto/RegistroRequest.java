package cl.pagoflex.autenticacion.aplicacion.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RegistroRequest {

    private String nombreCompleto;

    private String correo;

    private String contrasena;

    private String rut;

    private String rol; // "USUARIO" o "EJECUTIVO"
    
    private UUID empresaId; // Solo para ejecutivos
}
  

