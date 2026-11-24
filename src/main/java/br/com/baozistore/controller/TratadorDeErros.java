package br.com.baozistore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Devolve os erros da API em um JSON simples, com a mensagem em português. */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> statusException(ResponseStatusException e) {
        return resposta(e.getStatusCode(), List.of(e.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException e) {
        List<String> mensagens = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, mensagens);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST, List.of("JSON inválido ou com tipo de campo incorreto"));
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatusCode status, List<String> mensagens) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("dataHora", LocalDateTime.now().withNano(0));
        corpo.put("status", status.value());
        corpo.put("erros", mensagens);
        return ResponseEntity.status(status).body(corpo);
    }
}
