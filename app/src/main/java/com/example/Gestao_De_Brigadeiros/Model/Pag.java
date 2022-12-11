package com.example.Gestao_De_Brigadeiros.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

//Criando e nomeando a tabela pag e setando a chave estrangeira
@Entity(tableName = "Pag",indices = @Index(value = {"mensagem_alerta"}, unique = true))

public class Pag {

    //Setando a chave primaria
    @PrimaryKey(autoGenerate = true)
    private int id;

    //Definindo Campos que não podem ser nulos
    @NonNull
    private String mensagem_alerta;

    //Salvando o nome do método de pagamento no banco
    public Pag(String mensagem_alerta) {
        setMensagem_alerta(mensagem_alerta);
    }

    //Construção dos getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getMensagem_alerta() {
        return mensagem_alerta;
    }

    public void setMensagem_alerta(@NonNull String mensagem_alerta) {
        this.mensagem_alerta = mensagem_alerta;
    }

    //Retorno na tela
    @Override
    public String toString() {return getMensagem_alerta();}
}
