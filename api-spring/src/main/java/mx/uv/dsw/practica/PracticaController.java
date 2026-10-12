package mx.uv.dsw.practica;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST del recurso principal (practicas) del proyecto PR10.
 *
 * GET  /api/practicas          -> 200, lista JSON de practicas.
 * POST /api/practicas          -> 201 si es valida y esta autorizada;
 *                                 401 sin cabecera X-Lab-Key;
 *                                 400 si el cuerpo no cumple las validaciones
 *                                 (organizacionId, estudianteId, titulo y estado).
 * GET  /api/practicas/health   -> 200, sonda de salud.
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

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-Lab-Key", required = false) String key,
            @Valid @RequestBody Practica practica) {
        if (!labKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("X-Lab-Key requerida");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(practica));
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
