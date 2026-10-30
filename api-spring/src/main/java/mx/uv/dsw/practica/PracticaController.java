package mx.uv.dsw.practica;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST del recurso principal (practicas) del proyecto PR10.
 *
 * GET   /api/practicas            -> 200, lista JSON de practicas.
 * GET   /api/practicas/{id}       -> 200 con la practica; 404 si no existe.
 * POST  /api/practicas            -> 201 si es valida y esta autorizada;
 *                                    401 sin cabecera X-Lab-Key;
 *                                    400 si el cuerpo no cumple las validaciones.
 * PATCH /api/practicas/{id}/estado-> 200 cambia el estado; 401 sin clave;
 *                                    400 estado invalido; 404 si no existe.
 * GET   /api/practicas/health     -> 200, sonda de salud.
 *
 * Las escrituras exigen la cabecera X-Lab-Key, que se toma de configuracion y
 * no es un secreto real. Las consultas (GET) son abiertas en el entorno de laboratorio.
 */
@RestController
@RequestMapping("/api/practicas")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8088"})
public class PracticaController {

    private final PracticaRepository repo;
    private final String labKey;

    public PracticaController(PracticaRepository repo,
                              @Value("${lab.api-key:lab-only-key}") String labKey) {
        this.repo = repo;
        this.labKey = labKey;
    }

    @GetMapping
    public List<Practica> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Practica one(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Practica " + id + " no encontrada"));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-Lab-Key", required = false) String key,
            @Valid @RequestBody Practica practica) {
        if (!labKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("X-Lab-Key requerida");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(practica));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @RequestHeader(value = "X-Lab-Key", required = false) String key,
            @PathVariable Long id,
            @Valid @RequestBody CambioEstado cambio) {
        if (!labKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("X-Lab-Key requerida");
        }
        Practica practica = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Practica " + id + " no encontrada"));
        practica.setEstado(cambio.getEstado());
        return ResponseEntity.ok(repo.save(practica));
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
