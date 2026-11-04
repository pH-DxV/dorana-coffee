package br.unitins.topicos1.lgc.cafe.service;

import java.util.List;

import br.unitins.topicos1.lgc.cafe.dto.CafeDTO;
import br.unitins.topicos1.lgc.cafe.dto.CafeDTOResponse;

public interface CafeService {

    // --- CRUD ---
    CafeDTOResponse create(CafeDTO dto);
    CafeDTOResponse update(Long id, CafeDTO dto);
    void delete(Long id);
    
    // --- CONSULTAS ---
    CafeDTOResponse findById(Long id);
    List<CafeDTOResponse> findAll();
    List<CafeDTOResponse> findByNome(String nome);
    List<CafeDTOResponse> findByPontuacao(Integer minSCA, Integer maxSCA);

    // --- MÉTODOS DE ESTOQUE ---
    // Estes métodos são usados principalmente por outros serviços (como PedidoService),
    // mas devem estar no contrato para serem injetáveis.
    boolean verificarEstoque(Long idCafe, Integer quantidade);
    void baixarEstoque(Long idCafe, Integer quantidade);
}