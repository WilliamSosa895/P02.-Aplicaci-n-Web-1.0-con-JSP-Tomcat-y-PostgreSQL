package mx.uv.dsw.practica;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce los errores a respuestas JSON controladas, con un cuerpo uniforme y
 * sin exponer trazas ni detalles internos (checklist OWASP: errores sin
 * informacion sensible). Las validaciones de entrada responden 400; un recurso
 * inexistente responde 404.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private Map<String, Object> cuerpo(HttpStatus estado, String mensaje) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", estado.value());
        m.put("error", estado.getReasonPhrase());
        m.put("message", mensaje);
        return m;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> noValido(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Datos de entrada no validos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(cuerpo(HttpStatus.BAD_REQUEST, detalle));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(cuerpo(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es valido"));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(cuerpo(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
}
