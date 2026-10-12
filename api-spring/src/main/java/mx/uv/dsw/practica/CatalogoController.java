package mx.uv.dsw.practica;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lecturas de catalogo del dominio PR10: organizaciones y estudiantes.
 * Permiten poblar el formulario del cliente Angular y evidencian las
 * entidades relacionadas con la practica.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8088"})
public class CatalogoController {

    private final OrganizacionRepository organizaciones;
    private final EstudianteFicticioRepository estudiantes;

    public CatalogoController(OrganizacionRepository o, EstudianteFicticioRepository e) {
        this.organizaciones = o;
        this.estudiantes = e;
    }

    @GetMapping("/api/organizaciones")
    public List<Organizacion> organizaciones() {
        return organizaciones.findAll();
    }

    @GetMapping("/api/estudiantes")
    public List<EstudianteFicticio> estudiantes() {
        return estudiantes.findAll();
    }
}
