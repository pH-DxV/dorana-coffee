package br.unitins.topicos1.lgc.marca.service;

import java.util.List;

import br.unitins.topicos1.lgc.marca.dto.MarcaDTO;
import br.unitins.topicos1.lgc.marca.dto.MarcaDTOResponse;

public interface MarcaService {

    List<MarcaDTOResponse> findAll();
    List<MarcaDTOResponse> findByNome(String nome);
    MarcaDTOResponse findById(Long id);

    MarcaDTOResponse create (MarcaDTO dto);
    MarcaDTOResponse update (Long id, MarcaDTO dto);
    void delete (Long id);
    

    
}
