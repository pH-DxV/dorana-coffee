package br.unitins.topicos1.lgc.cliente.service;

import java.util.List;
import br.unitins.topicos1.lgc.cliente.dto.ClienteDTO;
import br.unitins.topicos1.lgc.cliente.dto.ClienteDTOResponse;

public interface ClienteService {

    // --- MÉTODOS CRUD ---
    ClienteDTOResponse create(ClienteDTO dto);
    ClienteDTOResponse update(Long id, ClienteDTO dto);
    void delete(Long id);
    
    // --- MÉTODOS DE CONSULTA ---
    ClienteDTOResponse findById(Long id);
    List<ClienteDTOResponse> findAll();
    List<ClienteDTOResponse> findByNome(String nome);
}
