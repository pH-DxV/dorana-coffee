package br.unitins.topicos1.lgc.estoque.service;

public interface EstoqueService {
    
    // Cria o registro de estoque ao cadastrar um novo café
    void iniciarEstoque(Long idCafe, Integer quantidadeInicial);
    
    // Adiciona unidades (reabastecimento)
    void adicionarEstoque(Long idCafe, Integer quantidade);
    
    // Remove unidades (venda) - Lança erro se insuficiente
    void baixarEstoque(Long idCafe, Integer quantidade);
    
    // Consulta simples
    Integer consultarQuantidade(Long idCafe);

    // Consulta em lote para evitar N+1 queries
    java.util.Map<Long, Integer> consultarQuantidades(java.util.List<Long> cafeIds);

    // Atualizar estoque
    void atualizarEstoque(Long idCafe, Integer quantidade);

    // Excluir estoque
    void excluirEstoque(Long idCafe);
}