package br.com.baozistore.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Corpo JSON recebido em POST/PUT /pedidos, com os mesmos campos do DER:
 * { "clienteId": 1, "produtoId": 1, "quantidade": 10 }
 */
public record PedidoRequest(
        @NotNull(message = "clienteId é obrigatório") Long clienteId,
        @NotNull(message = "produtoId é obrigatório") Long produtoId,
        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade) {
}
