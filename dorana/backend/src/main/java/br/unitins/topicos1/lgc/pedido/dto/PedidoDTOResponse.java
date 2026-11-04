package br.unitins.topicos1.lgc.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTOResponse;
import br.unitins.topicos1.lgc.itemPedido.dto.ItemPedidoDTOResponse;
import br.unitins.topicos1.lgc.pagamento.dto.PagamentoResumoDTO;
import br.unitins.topicos1.lgc.pedido.model.Pedido;
import br.unitins.topicos1.lgc.pedido.model.PedidoStatus;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTOResponse;

public record PedidoDTOResponse(
    Long id,
    LocalDateTime dataHora,
    BigDecimal totalPedido,
    BigDecimal valorFrete,
    UsuarioDTOResponse usuario, // Mantemos o objeto completo ou o ID
    EnderecoDTOResponse enderecoEntrega,
    List<ItemPedidoDTOResponse> itens,
    PagamentoResumoDTO pagamento,
    PedidoStatus status
) {
    public static PedidoDTOResponse valueOf(Pedido pedido) {
        List<ItemPedidoDTOResponse> listaItens = (pedido.getItens() != null) ?
            pedido.getItens().stream().map(ItemPedidoDTOResponse::valueOf).toList() : 
            List.of();

        return new PedidoDTOResponse(
            pedido.getId(),
            pedido.getDataHora(),
            pedido.getTotalPedido(),
            pedido.getValorFrete(),
            UsuarioDTOResponse.valueOf(pedido.getUsuario()), // Aqui passamos o usuário
            EnderecoDTOResponse.valueOf(pedido.getEnderecoEntrega()),
            listaItens,
            PagamentoResumoDTO.valueOf(pedido.getPagamento()),
            pedido.getStatus()
        );
    }
}