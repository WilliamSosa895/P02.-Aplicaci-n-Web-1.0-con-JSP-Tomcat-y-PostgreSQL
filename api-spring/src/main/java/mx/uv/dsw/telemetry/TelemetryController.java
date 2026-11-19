package mx.uv.dsw.telemetry;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de telemetria IoT (P06, Web 4.0).
 *
 * POST /api/telemetry            -> 201 ingesta (requiere X-Lab-Key); 401 sin clave; 400 fuera de rango fisico.
 * GET  /api/telemetry            -> 200 historial completo (compatibilidad).
 * GET  /api/telemetry/latest     -> 200 ultimo valor por dispositivo + estado de conexion (tarjetas del dashboard).
 * GET  /api/telemetry/history/{deviceId} -> 200 historial temporal de un dispositivo.
 * GET  /api/telemetry/alerts     -> 200 lecturas en ALERTA o ANOMALA.
 * GET  /api/telemetry/health     -> 200 sonda de salud.
 *
 * La ingesta exige la cabecera X-Lab-Key (clave de laboratorio, no es un secreto real).
 */
@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8088"})
public class TelemetryController {

    private final TelemetryRepository repo;
    private final TelemetryService service;
    private final String labKey;

    public TelemetryController(TelemetryRepository repo, TelemetryService service,
                               @Value("${lab.api-key:lab-only-key}") String labKey) {
        this.repo = repo;
        this.service = service;
        this.labKey = labKey;
    }

    @GetMapping
    public List<Telemetry> all() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-Lab-Key", required = false) String key,
            @Valid @RequestBody Telemetry t) {
        if (!labKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("X-Lab-Key requerida");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ingestar(t));
    }

    @GetMapping("/latest")
    public List<DeviceStatus> latest() {
        return service.estadoDispositivos();
    }

    @GetMapping("/history/{deviceId}")
    public List<Telemetry> history(@PathVariable String deviceId) {
        return service.historial(deviceId);
    }

    @GetMapping("/alerts")
    public List<Telemetry> alerts() {
        return service.alertas();
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
