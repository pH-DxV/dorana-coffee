package br.unitins.topicos1.lgc.cafe.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unitins.topicos1.lgc.cafe.model.Cafe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class CafeRepository implements PanacheRepository<Cafe> {

    // Lista todos os cafés ativos trazendo os relacionamentos essenciais em 1 query (evita N+1)
    public List<Cafe> listAllComRelacionamentos() {
        return find("select distinct c from Cafe c " +
                    "left join fetch c.marca " +
                    "left join fetch c.categoriaDoCafe " +
                    "where c.ativo = true").list();
    }

    // Para regras de deleção de Marca/Categoria
    public long countByMarca(Long idMarca) {
        return count("marca.id = ?1", idMarca);
    }

    public long countByCategoriaDoCafe(Long idCategoria) {
        return count("categoriaDoCafe.id = ?1", idCategoria);
    }
    
    // Busca por nome (case-insensitive) trazendo relacionamentos
    public List<Cafe> findByNomeLike(String nome) {
        return find("select distinct c from Cafe c " +
                    "left join fetch c.marca " +
                    "left join fetch c.categoriaDoCafe " +
                    "where c.ativo = true and UPPER(c.nome) LIKE ?1", "%" + nome.toUpperCase() + "%").list();
    }
    
    // Busca por pontuação trazendo relacionamentos
    public List<Cafe> findByPontuacaoRange(Integer minSCA, Integer maxSCA) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder query = new StringBuilder("select distinct c from Cafe c " +
                                                "left join fetch c.marca " +
                                                "left join fetch c.categoriaDoCafe " +
                                                "where c.ativo = true "); 

        if (minSCA != null) {
            query.append("AND c.pontuacaoSCA >= :minSCA ");
            params.put("minSCA", minSCA);
        }

        if (maxSCA != null) {
            query.append("AND c.pontuacaoSCA <= :maxSCA ");
            params.put("maxSCA", maxSCA);
        }
        
        return find(query.toString(), params).list();
    }
}