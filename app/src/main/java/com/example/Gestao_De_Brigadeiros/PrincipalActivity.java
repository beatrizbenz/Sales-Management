package com.example.Gestao_De_Brigadeiros;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.Gestao_De_Brigadeiros.Model.Dados;
import com.example.Gestao_De_Brigadeiros.Persistencia.ControleDatabase;
import com.example.Gestao_De_Brigadeiros.Utils.UtilsGUI;

import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private ListView listViewCadastro;
    private ArrayAdapter <Dados> listaAdapter;
    private List<Dados> listaDados;

    private static final String ARQUIVO = "com.example.Gestao_De_Brigadeiros.PREFERENCIAS_MODE";
    private boolean darkMode = false;
    private static final String MODO_NOTURNO = "MODO_NOTURNO";
    private static final int REQUEST_NOVO_CADASTRO = 1;
    private static final int REQUEST_ALTERAR_CADASTRO = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        listViewCadastro = findViewById(R.id.listViewFormasDePagamento);

        lerPreferencia();

        listViewCadastro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Dados dados = (Dados) parent.getItemAtPosition(position);

                DadosActivity.alterarCadastro(PrincipalActivity.this, REQUEST_ALTERAR_CADASTRO, dados);
            }
        });
        carregaLista();

        registerForContextMenu(listViewCadastro);

    }

    private void carregaLista(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ControleDatabase database = ControleDatabase.getDatabase(PrincipalActivity.this);

                listaDados = database.DadosDAO().queryAll();

                PrincipalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(PrincipalActivity.this, android.R.layout.simple_list_item_1, listaDados);

                        listViewCadastro.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void lerPreferencia(){

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
         darkMode = shared.getBoolean(MODO_NOTURNO,darkMode);
        ativaModoNoturno();
    }

    private void salvarPreferencia(boolean dark){

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(MODO_NOTURNO, dark);

        editor.commit();

        darkMode = dark;

        ativaModoNoturno();
    }

    private void ativaModoNoturno(){

        if(darkMode == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }


    private void excluirCadastro(final Dados dados){

        String mensagem = getString(R.string.deseja_apagar) + "\n" + dados.getNome();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                    ControleDatabase database = ControleDatabase.getDatabase(PrincipalActivity.this);

                                    database.DadosDAO().delete(dados);

                                    PrincipalActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listaAdapter.remove(dados);
                                    }
                                });
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.principal_opcoes,menu);
        MenuItem switch_modo_black = menu.findItem(R.id.switch_no_menu);

        final Switch widget_switch_modo_black = switch_modo_black.getActionView().findViewById(R.id.switch_action_bar_switch);

        widget_switch_modo_black.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    salvarPreferencia(true);
                }else{
                    salvarPreferencia(false);
                }


            }
        });
        return true;
    }

    private void verificaFormaDePagamento(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ControleDatabase database = ControleDatabase.getDatabase(PrincipalActivity.this);

                int total = database.PagDao().total();

                if (total == 0){

                   PrincipalActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.avisoErro(PrincipalActivity.this, R.string.selecione_o_metodo_de_pagamento);
                        }
                    });

                    return;
                }

                DadosActivity.novoCadastro(PrincipalActivity.this, REQUEST_NOVO_CADASTRO);
            }
        });
    }
    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {

        MenuItem switch_modo = menu.findItem(R.id.switch_no_menu);

        Switch widget_switch = switch_modo.getActionView().findViewById(R.id.switch_action_bar_switch);

        widget_switch.setChecked(darkMode);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Dados controle = (Dados) listViewCadastro.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemEditar:
                DadosActivity.alterarCadastro(this, REQUEST_ALTERAR_CADASTRO, controle);
                return true;

            case R.id.menuItemApagar:
                excluirCadastro(controle);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItemAdicionar:
                verificaFormaDePagamento();
                return true;
            case R.id.menuItemSobre:
                SobreActivity.sobre(this);
                return true;
            case R.id.switch_action_bar_switch:
                salvarPreferencia(darkMode);
                return true;
            case R.id.FormasDePagamentos:
                PagamentoActivity.abrir(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_NOVO_CADASTRO || requestCode == REQUEST_ALTERAR_CADASTRO) && resultCode == Activity.RESULT_OK) {

            carregaLista();
        }
    }

}