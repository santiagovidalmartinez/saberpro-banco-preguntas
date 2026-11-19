package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JustificacionTest {

    @Test
    void debeAceptarUnTextoDeAlMenos15Caracteres() {
        Justificacion j = new Justificacion("  Texto con quince car  ");
        assertEquals("Texto con quince car", j.getTexto(), "Se recorta (trim)");
    }

    @Test
    void debeRechazarMenosDe15Caracteres() {
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("Catorce chars!"));
        assertEquals("La justificación debe contener al menos 15 caracteres.", ex.getMessage());
    }

    @Test
    void losEspaciosDeRelleno_NoCuentanParaLaLongitud() {
        assertThrows(ReglaNegocioException.class, () -> new Justificacion("   corta               "));
    }

    @Test
    void debeRechazarNuloYVacio() {
        assertThrows(ReglaNegocioException.class, () -> new Justificacion(null));
        assertThrows(ReglaNegocioException.class, () -> new Justificacion(""));
    }

    @Test
    void debeRechazarPalabrasProhibidasSinImportarTildesNiMayusculas() {
        assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("Es la respuesta OBVIAMENTE correcta"));
        assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("Se elige así porque sí, sin más análisis"));
    }

    @Test
    void dosJustificacionesConElMismoTextoSonIguales() {
        assertEquals(new Justificacion("Argumento pedagógico válido"),
                new Justificacion("Argumento pedagógico válido "));
    }
}
