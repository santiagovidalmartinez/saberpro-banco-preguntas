package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comportamiento REST completo de la aplicación. Se ejecuta dos veces: con JpaQuestionAdapter y con
 * InMemoryQuestionAdapter. Las pruebas son idénticas: el dominio no cambia ni una línea.
 */
abstract class QuestionApiContractTest {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected PreguntaRepositoryPort adaptadorActivo;

    private static final String PREGUNTA_VALIDA = """
            {"enunciado":"¿Cuál es la capital de Colombia?",
             "justificacion":"Bogotá es la capital y sede del gobierno nacional.",
             "opciones":[{"texto":"Medellín","correcta":false},{"texto":"Bogotá","correcta":true},
                         {"texto":"Cali","correcta":false},{"texto":"Popayán","correcta":false}]}""";

    private String crearValida() throws Exception {
        MvcResult r = mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(PREGUNTA_VALIDA))
                .andExpect(status().isCreated()).andReturn();
        return new ObjectMapper().readTree(r.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void creaUnaPreguntaYResponde201ConEstadoBorrador() throws Exception {
        mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(PREGUNTA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.opciones", hasSize(4)));
    }

    @Test
    void rechazaUnaJustificacionCortaConError400() throws Exception {
        String malo = PREGUNTA_VALIDA.replace("Bogotá es la capital y sede del gobierno nacional.", "corta");
        mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(malo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La justificación debe contener al menos 15 caracteres."));
    }

    @Test
    void rechazaUnEnunciadoVacioConError400() throws Exception {
        String malo = PREGUNTA_VALIDA.replace("¿Cuál es la capital de Colombia?", "   ");
        mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(malo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enviaARevisionUnaPreguntaCompletaYResponde202() throws Exception {
        String id = crearValida();
        mvc.perform(post("/api/preguntas/" + id + "/enviar-revision"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.operationId").isNotEmpty())
                .andExpect(jsonPath("$.pregunta.estado").value("PENDIENTE_REVISION"));
        mvc.perform(get("/api/preguntas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE_REVISION"));
    }

    @Test
    void noEnviaARevisionSinCuatroOpcionesYResponde400() throws Exception {
        String tresOpciones = """
                {"enunciado":"¿Enunciado válido?","justificacion":"Justificación suficientemente larga",
                 "opciones":[{"texto":"A","correcta":true},{"texto":"B","correcta":false},{"texto":"C","correcta":false}]}""";
        MvcResult r = mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content(tresOpciones))
                .andExpect(status().isCreated()).andReturn();
        String id = new ObjectMapper().readTree(r.getResponse().getContentAsString()).get("id").asText();

        mvc.perform(post("/api/preguntas/" + id + "/enviar-revision"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Debe tener exactamente 4 opciones de respuesta."));
        mvc.perform(get("/api/preguntas/" + id)).andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    void apruebaYRechazaUnaPreguntaPendiente() throws Exception {
        String a = crearValida();
        String b = crearValida();
        mvc.perform(post("/api/preguntas/" + a + "/enviar-revision")).andExpect(status().isAccepted());
        mvc.perform(post("/api/preguntas/" + b + "/enviar-revision")).andExpect(status().isAccepted());
        mvc.perform(post("/api/preguntas/" + a + "/aprobar"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("APROBADA"));
        mvc.perform(post("/api/preguntas/" + b + "/rechazar"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }

    @Test
    void noApruebaUnaPreguntaEnBorradorYResponde400() throws Exception {
        String id = crearValida();
        mvc.perform(post("/api/preguntas/" + id + "/aprobar")).andExpect(status().isBadRequest());
    }

    @Test
    void preguntaInexistenteResponde404() throws Exception {
        mvc.perform(get("/api/preguntas/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void identificadorMalFormadoResponde400() throws Exception {
        mvc.perform(get("/api/preguntas/no-es-un-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void jsonInvalidoResponde400() throws Exception {
        mvc.perform(post("/api/preguntas").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listaLasPreguntasCreadas() throws Exception {
        crearValida();
        mvc.perform(get("/api/preguntas")).andExpect(status().isOk());
        assertFalse(adaptadorActivo.findAll().isEmpty());
    }
}
