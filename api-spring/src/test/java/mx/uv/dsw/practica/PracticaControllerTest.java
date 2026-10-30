package mx.uv.dsw.practica;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PracticaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    OrganizacionRepository organizaciones;

    @Autowired
    EstudianteFicticioRepository estudiantes;

    private static final String VALIDA =
        "{\"organizacionId\":1,\"estudianteId\":1,"
        + "\"titulo\":\"Practica de desarrollo web\",\"estado\":\"POSTULADA\"}";

    /** Crea una organizacion y un estudiante para poder registrar practicas. */
    private void sembrarCatalogos() {
        if (organizaciones.count() == 0) {
            Organizacion o = new Organizacion();
            o.setNombre("Organizacion Prueba");
            o.setSector("Software");
            organizaciones.save(o);
        }
        if (estudiantes.count() == 0) {
            EstudianteFicticio e = new EstudianteFicticio();
            e.setNombre("Estudiante Prueba");
            e.setPrograma("Ingenieria de Software");
            estudiantes.save(e);
        }
    }

    // ---------- Alta (POST) ----------

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
        sembrarCatalogos();
        mvc.perform(post("/api/practicas").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content(VALIDA))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.titulo").value("Practica de desarrollo web"));
    }

    // ---------- Consulta (GET) ----------

    @Test
    void listaPracticas() throws Exception {
        mvc.perform(get("/api/practicas"))
           .andExpect(status().isOk());
    }

    @Test
    void consultaInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/practicas/999999"))
           .andExpect(status().isNotFound());
    }

    @Test
    void saludResponde() throws Exception {
        mvc.perform(get("/api/practicas/health"))
           .andExpect(status().isOk());
    }

    // ---------- Cambio de estado (PATCH) ----------

    private long crearPractica() throws Exception {
        sembrarCatalogos();
        MvcResult r = mvc.perform(post("/api/practicas").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content(VALIDA))
            .andExpect(status().isCreated()).andReturn();
        JsonNode node = json.readTree(r.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    @Test
    void cambiaEstadoAutorizado() throws Exception {
        long id = crearPractica();
        mvc.perform(patch("/api/practicas/" + id + "/estado").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ACEPTADA\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.estado").value("ACEPTADA"));
    }

    @Test
    void cambioEstadoSinClaveRechazado() throws Exception {
        long id = crearPractica();
        mvc.perform(patch("/api/practicas/" + id + "/estado")
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ACEPTADA\"}"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void cambioEstadoInvalidoRechazado() throws Exception {
        long id = crearPractica();
        mvc.perform(patch("/api/practicas/" + id + "/estado").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"NO_EXISTE\"}"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void cambioEstadoPracticaInexistente404() throws Exception {
        mvc.perform(patch("/api/practicas/999999/estado").header("X-Lab-Key", "lab-only-key")
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ACEPTADA\"}"))
           .andExpect(status().isNotFound());
    }
}
