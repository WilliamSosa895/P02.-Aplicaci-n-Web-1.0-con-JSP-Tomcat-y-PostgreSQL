package mx.uv.dsw.telemetry;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryControllerTest {

    @Autowired
    MockMvc mvc;

    private static final String KEY = "lab-only-key";

    @Test
    void rechazaPeticionSinClave() throws Exception {
        mvc.perform(post("/api/telemetry").contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\":\"sensor-01\",\"value\":24.5}"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaValorFueraDeRango() throws Exception {
        mvc.perform(post("/api/telemetry").header("X-Lab-Key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\":\"sensor-01\",\"value\":999}"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void creaTelemetriaAutorizada() throws Exception {
        mvc.perform(post("/api/telemetry").header("X-Lab-Key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\":\"sensor-01\",\"variable\":\"temperatura\",\"unit\":\"C\",\"value\":24.5}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.deviceId").value("sensor-01"))
           .andExpect(jsonPath("$.quality").value("NORMAL"));
    }

    @Test
    void clasificaAlerta() throws Exception {
        mvc.perform(post("/api/telemetry").header("X-Lab-Key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\":\"sensor-02\",\"variable\":\"temperatura\",\"unit\":\"C\",\"value\":31.5}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.quality").value("ALERTA"));
    }

    @Test
    void clasificaAnomala() throws Exception {
        mvc.perform(post("/api/telemetry").header("X-Lab-Key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\":\"sensor-03\",\"variable\":\"temperatura\",\"unit\":\"C\",\"value\":85.0}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.quality").value("ANOMALA"));
    }

    @Test
    void latestResponde() throws Exception {
        mvc.perform(get("/api/telemetry/latest")).andExpect(status().isOk());
    }

    @Test
    void alertsResponde() throws Exception {
        mvc.perform(get("/api/telemetry/alerts")).andExpect(status().isOk());
    }

    @Test
    void saludResponde() throws Exception {
        mvc.perform(get("/api/telemetry/health")).andExpect(status().isOk());
    }
}
