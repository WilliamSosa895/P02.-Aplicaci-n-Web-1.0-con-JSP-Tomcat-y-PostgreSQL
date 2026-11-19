package mx.uv.dsw.telemetry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de telemetria con consultas temporales para el historial y el dashboard. */
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

    /** Historial de un dispositivo, mas reciente primero. */
    List<Telemetry> findByDeviceIdOrderByCapturedAtDesc(String deviceId);

    /** Ultimas lecturas globales, mas reciente primero. */
    List<Telemetry> findTop200ByOrderByCapturedAtDesc();

    /** Lecturas marcadas con una calidad (ALERTA / ANOMALA) para el panel de alertas. */
    List<Telemetry> findByQualityInOrderByCapturedAtDesc(List<String> qualities);
}
