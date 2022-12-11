package com.example.Gestao_De_Brigadeiros.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


import java.util.Date;

// Criando o Banco de Dados - SqlLite (Nativo do Android) e setando as chaves
@Entity(tableName = "Dados", foreignKeys = @ForeignKey(entity = Pag.class, parentColumns =  "id", childColumns = "id_forma_pagamento"))
public class Dados {
    public static final  int ALUNO = 1;
    public static final  int FUNCIONARIO = 2;
    public static final int COMUNIDADE = 3;

    //Setando a chave primaria
    @PrimaryKey(autoGenerate = true)
    private int id;

    //Definindo Campos que não podem ser nulos
    @NonNull
    private String nome;
    private String telefone;
    private String Valor;
    private Date dataDePagamento;
    private Date dataOrdemGerada;
    private int classificacao;
    private boolean cliente_especial;

    //Puxando a chave da outra tabela "Forma de Pagamento"
    @ColumnInfo(index = true)
    private int id_forma_pagamento;


    //Construção dos getters & setters
    public Dados(String nome){
        setNome(nome);
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(int classificacao) {
        this.classificacao = classificacao;
    }

    public boolean isCliente_especial() {return cliente_especial;}

    public void setCliente_especial(boolean cliente_especial) {this.cliente_especial = cliente_especial;}

    public String getValor() {return Valor;}

    public void setValor(String valor) {this.Valor = valor;}

    public int getId_forma_pagamento() {return id_forma_pagamento;}

    public void setId_forma_pagamento(int id_forma_pagamento) {this.id_forma_pagamento = id_forma_pagamento;}

    public Date getDataDePagamento() {return dataDePagamento;}

    public void setDataDePagamento(Date dataDePagamento) {this.dataDePagamento = dataDePagamento;}

    public Date getDataOrdemGerada() {return dataOrdemGerada;}

    public void setDataOrdemGerada(Date dataOrdemGerada) {this.dataOrdemGerada = dataOrdemGerada;}


    //Ordem de listagem na List View
    @Override
    public String toString(){
       return getNome() + "\n" + getTelefone() + "\n" + isCliente_especial() + "\n" + getClassificacao() + "\n" + getId_forma_pagamento() + "\n" + getDataDePagamento() + "\n" + getValor() +"\n";

    }


}