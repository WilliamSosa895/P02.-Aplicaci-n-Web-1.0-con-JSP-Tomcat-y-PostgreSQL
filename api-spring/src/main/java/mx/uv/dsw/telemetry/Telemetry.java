package mx.uv.dsw.telemetry;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Lectura de telemetria IoT (P06, Web 4.0). Historial en PostgreSQL.
 * Campos minimos requeridos por R06: dispositivo, fecha-hora, variable, unidad y valor.
 * La "calidad" (NORMAL/ALERTA/ANOMALA) se calcula al ingresar y se almacena para el dashboard.
 */
@Entity
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 64)
    private String deviceId;

    /** Magnitud medida (p. ej. temperatura, humedad). Opcional por compatibilidad. */
    @Size(max = 40)
    private String variable;

    /** Unidad de la medida (p. ej. C, %). Opcional por compatibilidad. */
    @Size(max = 16)
    private String unit;

    @NotNull
    @DecimalMin("-100.0")
    @DecimalMax("200.0")
    @Column(name = "measurement_value")
    private Double value;

    /** Resultado del analisis de reglas. Lo asigna el servidor. */
    @Size(max = 16)
    private String quality;

    private Instant capturedAt = Instant.now();

    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String v) { deviceId = v; }
    public String getVariable() { return variable; }
    public void setVariable(String v) { variable = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { unit = v; }
    public Double getValue() { return value; }
    public void setValue(Double v) { value = v; }
    public String getQuality() { return quality; }
    public void setQuality(String v) { quality = v; }
    public Instant getCapturedAt() { return capturedAt; }
    public void setCapturedAt(Instant v) { capturedAt = v; }
}
