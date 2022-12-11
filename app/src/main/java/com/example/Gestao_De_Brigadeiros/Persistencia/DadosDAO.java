package com.example.Gestao_De_Brigadeiros.Persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.Gestao_De_Brigadeiros.Model.Dados;

import java.util.List;

//Expressões Sql para Manter na tabela Dados
@Dao
public interface DadosDAO {

    @Insert
    long insert(Dados dados);

    @Delete
    void delete(Dados dados);

    @Update
    void update(Dados dados);

    @Query("SELECT * FROM dados WHERE id = :id")
    Dados queryForId(long id);

    @Query("SELECT * FROM dados ORDER BY nome ASC")
    List<Dados> queryAll();

    @Query("SELECT count(*) FROM dados WHERE id_forma_pagamento = :id LIMIT 1")
    int queryForFormapagId(long id);
}
