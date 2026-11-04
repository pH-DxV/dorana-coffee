package br.unitins.topicos1.lgc.pagamento.dto;

import jakarta.validation.constraints.NotNull;

public record PagamentoBoletoDTO(
    @NotNull(message = "O ID do Pedido é obrigatório.")
    Long idPedido
) {}