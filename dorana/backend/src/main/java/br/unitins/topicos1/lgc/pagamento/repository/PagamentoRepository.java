package br.unitins.topicos1.lgc.pagamento.repository;

import java.util.List;

import br.unitins.topicos1.lgc.pagamento.model.Pagamento;
import br.unitins.topicos1.lgc.usuario.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagamentoRepository implements PanacheRepository<Pagamento> {
    
    // Busca pagamentos de um usuário através do pedido
    public List<Pagamento> findByUsuario(Usuario usuario) {
        return find("pedido.usuario = ?1", usuario).list();
    }
}