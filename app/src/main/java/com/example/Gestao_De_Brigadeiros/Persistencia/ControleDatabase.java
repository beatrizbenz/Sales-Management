package com.example.Gestao_De_Brigadeiros.Persistencia;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.Gestao_De_Brigadeiros.Model.Dados;
import com.example.Gestao_De_Brigadeiros.Model.Pag;
import com.example.Gestao_De_Brigadeiros.R;

import java.util.concurrent.Executors;

//Inicia o banco Cria o relacionamento entre as duas tabelas "DADOS" e "PAG"
@Database(entities = {Dados.class, Pag.class}, version = 1, exportSchema = false)@TypeConverters({Converters.class})
public abstract class ControleDatabase extends RoomDatabase {

    public abstract DadosDAO DadosDAO();

    public abstract PagDAO PagDao();

    private static ControleDatabase instance;

    public static ControleDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (ControleDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder  builder = Room.databaseBuilder(context, ControleDatabase.class, "controle.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carrega_sp_metodos_pagamento(context);
                                }
                            });
                        }
                    });
                    instance = (ControleDatabase) builder.build();
                }
            }
        }
        return instance;
    }

    private static void carrega_sp_metodos_pagamento(final Context context){

        String[] formas = context.getResources().getStringArray(R.array.formas_de_pagamento);

        for (String forma : formas) {

            Pag formadepagamento = new Pag(forma);

            instance.PagDao().insert(formadepagamento);
        }
    }
}
