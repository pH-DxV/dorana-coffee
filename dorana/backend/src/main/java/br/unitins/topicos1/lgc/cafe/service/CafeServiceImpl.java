package br.unitins.topicos1.lgc.cafe.service;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.topicos1.lgc.cafe.dto.CafeDTO;
import br.unitins.topicos1.lgc.cafe.dto.CafeDTOResponse;
import br.unitins.topicos1.lgc.cafe.model.Cafe;
import br.unitins.topicos1.lgc.cafe.repository.CafeRepository;
import br.unitins.topicos1.lgc.categoriaDoCafe.model.CategoriaDoCafe;
import br.unitins.topicos1.lgc.categoriaDoCafe.repository.CategoriaDoCafeRepository;
import br.unitins.topicos1.lgc.estoque.service.EstoqueService;
import br.unitins.topicos1.lgc.marca.model.Marca;
import br.unitins.topicos1.lgc.marca.repository.MarcaRepository;
import br.unitins.topicos1.lgc.nivelDeTorra.model.NivelDeTorra;
import br.unitins.topicos1.lgc.tratamento.model.Tratamento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CafeServiceImpl implements CafeService {

    @Inject
    CafeRepository cafeRepository;

    @Inject
    MarcaRepository marcaRepository;

    @Inject
    CategoriaDoCafeRepository categoriaRepository;

    @Inject
    EstoqueService estoqueService; 

    // --- Método Auxiliar para Conversão em Lote (Eliminação de N+1) ---
    private List<CafeDTOResponse> converterListaParaDTO(List<Cafe> cafes) {
        if (cafes == null || cafes.isEmpty()) {
            return List.of();
        }

        List<Long> ids = cafes.stream().map(Cafe::getId).collect(Collectors.toList());
        java.util.Map<Long, Integer> saldos = estoqueService.consultarQuantidades(ids);

        return cafes.stream()
                .map(cafe -> CafeDTOResponse.valueOf(cafe, saldos.getOrDefault(cafe.getId(), 0)))
                .collect(Collectors.toList());
    }
    // ------------------------------------------------------------------

    @Override
    public boolean verificarEstoque(Long idCafe, Integer quantidade) {
        Integer saldoAtual = estoqueService.consultarQuantidade(idCafe);
        return saldoAtual >= quantidade;
    }

    @Override
    @Transactional
    public void baixarEstoque(Long idCafe, Integer quantidade) {
        estoqueService.baixarEstoque(idCafe, quantidade);
    }

    @Override
    @Transactional
    public CafeDTOResponse create(CafeDTO dto) {
        Cafe cafe = new Cafe();
        
        cafe.setNome(dto.nome());
        cafe.setDescricao(dto.descricao());
        
        Marca marca = marcaRepository.findById(dto.idMarca());
        if (marca == null) throw new NotFoundException("Marca não encontrada.");
        cafe.setMarca(marca);
        
        CategoriaDoCafe categoria = categoriaRepository.findById(dto.idCategoriaDoCafe());
        if (categoria == null) throw new NotFoundException("Categoria do Café não encontrada.");
        cafe.setCategoriaDoCafe(categoria);
        
        cafe.setNivelDeTorra(NivelDeTorra.valueOf(dto.idNivelDeTorra()));
        cafe.setTratamento(Tratamento.valueOf(dto.idTratamento()));
        
        cafe.setNotasSensoriais(dto.notasSensoriais());
        cafe.setPontuacaoSCA(dto.pontuacaoSCA());
        cafe.setPreco(dto.preco());
        cafe.setPeso(dto.peso());
        
        // Persiste o produto (sem o campo estoque, pois foi removido da entidade)
        cafeRepository.persist(cafe);
        
        // Inicia o estoque na entidade separada (usando o ID do café recém-criado)
        estoqueService.iniciarEstoque(cafe.getId(), dto.estoque());
        
        // Retorna o DTO usando o valor inicial que passamos
        return CafeDTOResponse.valueOf(cafe, dto.estoque());
    }

    @Override
    @Transactional
    public CafeDTOResponse update(Long id, CafeDTO dto) {
        Cafe cafe = cafeRepository.findById(id);
        if (cafe == null) throw new NotFoundException("Café não encontrado.");

        cafe.setNome(dto.nome());
        cafe.setDescricao(dto.descricao());

        Marca marca = marcaRepository.findById(dto.idMarca());
        if (marca == null) throw new NotFoundException("Marca não encontrada.");
        cafe.setMarca(marca);
        
        CategoriaDoCafe categoria = categoriaRepository.findById(dto.idCategoriaDoCafe());
        if (categoria == null) throw new NotFoundException("Categoria do Café não encontrada.");
        cafe.setCategoriaDoCafe(categoria);
        
        cafe.setNivelDeTorra(NivelDeTorra.valueOf(dto.idNivelDeTorra()));
        cafe.setTratamento(Tratamento.valueOf(dto.idTratamento()));
        
        cafe.setNotasSensoriais(dto.notasSensoriais());
        cafe.setPontuacaoSCA(dto.pontuacaoSCA());
        cafe.setPreco(dto.preco());
        cafe.setPeso(dto.peso());
        
        // Busca o saldo atual para retornar no DTO (o update de produto não altera estoque)
        estoqueService.atualizarEstoque(id, dto.estoque());

        Integer saldo = estoqueService.consultarQuantidade(id);

        return CafeDTOResponse.valueOf(cafe, saldo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cafe cafe = cafeRepository.findById(id);
        if (cafe == null) {
            throw new NotFoundException("Café não encontrado.");
        }

        // Soft delete: inativa o café em vez de exclusão física para não violar integridade referencial com pedidos
        cafe.setAtivo(false);
    }

    @Override
    public CafeDTOResponse findById(Long id) {
        Cafe cafe = cafeRepository.findById(id);
        if (cafe == null) throw new NotFoundException("Café não encontrado.");
        
        Integer saldo = estoqueService.consultarQuantidade(id);
        return CafeDTOResponse.valueOf(cafe, saldo);
    }

    @Override
    public List<CafeDTOResponse> findAll() {
        return converterListaParaDTO(cafeRepository.listAllComRelacionamentos());
    }

    @Override
    public List<CafeDTOResponse> findByNome(String nome) {
        return converterListaParaDTO(cafeRepository.findByNomeLike(nome));
    }

    @Override
    public List<CafeDTOResponse> findByPontuacao(Integer minSCA, Integer maxSCA) {
        return converterListaParaDTO(cafeRepository.findByPontuacaoRange(minSCA, maxSCA));
    }
}