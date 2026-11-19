package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/** Traduce las excepciones del dominio a respuestas HTTP. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> reglaNegocio(ReglaNegocioException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PreguntaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> noEncontrada(PreguntaNoEncontradaException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> jsonInvalido(HttpMessageNotReadableException ex) {
        return Map.of("error", "El cuerpo de la petición no es un JSON válido.");
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, String> servicioNoDisponible(ServicioNoDisponibleException ex) {
        return Map.of("error", ex.getMessage());
    }
}
