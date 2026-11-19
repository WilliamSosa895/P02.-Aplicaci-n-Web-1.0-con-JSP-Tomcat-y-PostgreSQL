package mx.uv.dsw.telemetry;

import java.time.Instant;

/** Estado actual de un dispositivo para el dashboard: ultima lectura y conexion. */
public class DeviceStatus {
    private String deviceId;
    private String variable;
    private String unit;
    private Double value;
    private String quality;      // NORMAL / ALERTA / ANOMALA
    private Instant capturedAt;
    private String conexion;     // EN_LINEA / DESCONECTADO
    private long segundosDesdeUltima;

    public DeviceStatus(Telemetry t, String conexion, long segundos) {
        this.deviceId = t.getDeviceId();
        this.variable = t.getVariable();
        this.unit = t.getUnit();
        this.value = t.getValue();
        this.quality = t.getQuality();
        this.capturedAt = t.getCapturedAt();
        this.conexion = conexion;
        this.segundosDesdeUltima = segundos;
    }

    public String getDeviceId() { return deviceId; }
    public String getVariable() { return variable; }
    public String getUnit() { return unit; }
    public Double getValue() { return value; }
    public String getQuality() { return quality; }
    public Instant getCapturedAt() { return capturedAt; }
    public String getConexion() { return conexion; }
    public long getSegundosDesdeUltima() { return segundosDesdeUltima; }
}
