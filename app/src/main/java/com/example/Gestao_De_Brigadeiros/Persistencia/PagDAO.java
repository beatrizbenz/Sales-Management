package com.example.Gestao_De_Brigadeiros.Persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.Gestao_De_Brigadeiros.Model.Pag;

import java.util.List;

//Expressões Sql para Manter na tabela Pag
@Dao
public interface PagDAO {

    @Insert
    long insert(Pag formadepagamento);

    @Delete
    void delete(Pag formadepagamento);

    @Update
    void update(Pag formadepagamento);

    @Query("SELECT * FROM pag WHERE id = :id")
    Pag queryForId(long id);

    @Query("SELECT * FROM pag ORDER BY mensagem_alerta ASC")
    List<Pag> queryAll();

    @Query("SELECT * FROM pag WHERE mensagem_alerta = :forma ORDER BY mensagem_alerta ASC")
    List<Pag> queryForForma(String forma);

    @Query("SELECT count(*) FROM pag")
    int total();
}
