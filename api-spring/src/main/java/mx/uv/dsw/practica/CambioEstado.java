package mx.uv.dsw.practica;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Cuerpo de la operacion de cambio de estado de una practica.
 * Solo admite los estados permitidos del dominio; cualquier otro valor
 * produce una respuesta 400 controlada.
 */
public class CambioEstado {

    @NotBlank
    @Pattern(regexp = "POSTULADA|ACEPTADA|EN_CURSO|CONCLUIDA|RECHAZADA",
             message = "estado invalido")
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
