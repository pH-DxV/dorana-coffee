package br.unitins.topicos1.lgc.telefone.service;

import java.util.List;

import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTO;
import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTOResponse;

public interface TelefoneService {

    // --- CRUD ---
    TelefoneDTOResponse create(TelefoneDTO dto);
    TelefoneDTOResponse update(Long id, TelefoneDTO dto);
    void delete(Long id);

    // --- CONSULTAS ---
    TelefoneDTOResponse findById(Long id);
    List<TelefoneDTOResponse> findAll();
    List<TelefoneDTOResponse> findByUsuario(Long idUsuario);
}