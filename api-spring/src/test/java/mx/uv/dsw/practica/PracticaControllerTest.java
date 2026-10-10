package mx.uv.dsw.practica;

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
class PracticaControllerTest {

    @Autowired
    MockMvc mvc;

    private static final String VALIDA =
        "{\"organizacionId\":1,\"estudianteId\":1,"
        + "\"titulo\":\"Practica de desarrollo web\",\"estado\":\"POSTULADA\"}";

    @Test
    void rechazaPeticionSinClave() throws Exception {
        mvc.perform(post("/api/practicas")
                .contentType(MediaType.APPLICATION_JSON).content(VALIDA))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaEstadoInvalido() throws Exception {
        String cuerpo =
            "{\"organizacionId\":1,\"estudianteId\":1,"
            + "\"titulo\":\"Practica de desarrollo web\",\"estado\":\"NO_EXISTE\"}";
        mvc.perform(post("/api/practicas").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
           .andExpect(status().isBadRequest());
    }

    @Test
    void creaPracticaAutorizada() throws Exception {
        mvc.perform(post("/api/practicas").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content(VALIDA))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.titulo").value("Practica de desarrollo web"));
    }
}
