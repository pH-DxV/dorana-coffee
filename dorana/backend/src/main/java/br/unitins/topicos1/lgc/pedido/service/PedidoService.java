package br.unitins.topicos1.lgc.pedido.service;

import java.util.List;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTO;
import br.unitins.topicos1.lgc.pedido.dto.PedidoDTOResponse;

public interface PedidoService {
    PedidoDTOResponse create(PedidoDTO dto);
    PedidoDTOResponse findById(Long id);
    List<PedidoDTOResponse> findAll();
    List<PedidoDTOResponse> findByUsuario(Long idUsuario);
    PedidoDTOResponse cancelar(Long id);
}