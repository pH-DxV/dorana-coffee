package br.unitins.topicos1.lgc.estado.model;

import br.unitins.topicos1.lgc.defaultEntity.model.DefaultEntity;
import br.unitins.topicos1.lgc.regiao.model.Regiao;
import jakarta.persistence.Entity;

@Entity
public class Estado extends DefaultEntity{

    private String nome;
    private String sigla;
    private Regiao regiao;


    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getSigla() {
        return sigla;
    }
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
    public Regiao getRegiao() {
        return regiao;
    }
    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }
    
}
