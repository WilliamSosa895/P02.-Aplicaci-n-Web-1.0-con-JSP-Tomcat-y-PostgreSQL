package mx.uv.dsw.telemetry;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Logica de ingesta y de las vistas del dashboard de telemetria (P06). */
@Service
public class TelemetryService {

    private final TelemetryRepository repo;
    private final long umbralDesconexionSeg;

    public TelemetryService(TelemetryRepository repo,
                            @Value("${telemetry.offline-seconds:30}") long umbralDesconexionSeg) {
        this.repo = repo;
        this.umbralDesconexionSeg = umbralDesconexionSeg;
    }

    /** Persiste una lectura asignando valores por defecto y la calidad analizada. */
    public Telemetry ingestar(Telemetry t) {
        if (t.getVariable() == null || t.getVariable().isBlank()) t.setVariable("temperatura");
        if (t.getUnit() == null || t.getUnit().isBlank()) {
            t.setUnit("humedad".equalsIgnoreCase(t.getVariable()) ? "%" : "C");
        }
        if (t.getCapturedAt() == null) t.setCapturedAt(Instant.now());
        t.setQuality(Rules.clasificar(t.getVariable(), t.getValue()));
        return repo.save(t);
    }

    /** Ultima lectura por dispositivo con su estado de conexion (para las tarjetas del dashboard). */
    public List<DeviceStatus> estadoDispositivos() {
        Map<String, Telemetry> ultimoPorDispositivo = new LinkedHashMap<>();
        for (Telemetry t : repo.findTop200ByOrderByCapturedAtDesc()) {
            ultimoPorDispositivo.putIfAbsent(t.getDeviceId(), t);
        }
        List<DeviceStatus> salida = new ArrayList<>();
        Instant ahora = Instant.now();
        for (Telemetry t : ultimoPorDispositivo.values()) {
            long seg = Duration.between(t.getCapturedAt(), ahora).getSeconds();
            String conexion = seg > umbralDesconexionSeg ? "DESCONECTADO" : "EN_LINEA";
            salida.add(new DeviceStatus(t, conexion, Math.max(seg, 0)));
        }
        return salida;
    }

    public List<Telemetry> historial(String deviceId) {
        return repo.findByDeviceIdOrderByCapturedAtDesc(deviceId);
    }

    public List<Telemetry> alertas() {
        return repo.findByQualityInOrderByCapturedAtDesc(Arrays.asList(Rules.ALERTA, Rules.ANOMALA));
    }
}
