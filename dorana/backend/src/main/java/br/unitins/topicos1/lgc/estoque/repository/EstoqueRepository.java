package br.unitins.topicos1.lgc.estoque.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unitins.topicos1.lgc.estoque.model.Estoque;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EstoqueRepository implements PanacheRepository<Estoque> {
    
    public Estoque findByIdCafe(Long idCafe) {
        return find("cafe.id", idCafe).firstResult();
    }

    public Map<Long, Integer> findSaldosByCafeIds(List<Long> cafeIds) {
        if (cafeIds == null || cafeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Estoque> estoques = find("cafe.id in ?1", cafeIds).list();
        Map<Long, Integer> saldos = new HashMap<>();
        for (Estoque estoque : estoques) {
            if (estoque.getCafe() != null) {
                saldos.put(estoque.getCafe().getId(), estoque.getQuantidade());
            }
        }
        return saldos;
    }
}