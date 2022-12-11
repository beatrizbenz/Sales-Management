package com.example.Gestao_De_Brigadeiros;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Gestao_De_Brigadeiros.Model.Pag;
import com.example.Gestao_De_Brigadeiros.Persistencia.ControleDatabase;
import com.example.Gestao_De_Brigadeiros.Utils.UtilsGUI;

import java.util.List;

public class PagActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTexNovaFormaDePagamento;

    private int  modo;
    private Pag formadepagamento;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, PagActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Pag formadepagamento){

        Intent intent = new Intent(activity, PagActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, formadepagamento.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTexNovaFormaDePagamento = findViewById(R.id.editTextNovaFormaDePagamento);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
        }else{
            modo = NOVO;
        }

        if (modo == ALTERAR){

            setTitle(getString(R.string.alterar_forma_de_pagamento));

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    int id = bundle.getInt(ID);

                    ControleDatabase database = ControleDatabase.getDatabase(PagActivity.this);

                    formadepagamento = database.PagDao().queryForId(id);

                    PagActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTexNovaFormaDePagamento.setText(formadepagamento.getMensagem_alerta());
                        }
                    });
                }
            });

        }else{

            setTitle(getString(R.string.nova_forma_de_pagamento) );

            formadepagamento = new Pag("");
        }
    }

    private void salvar(){

        final String mensagem_alerta  = UtilsGUI.validaCampoTexto(this, editTexNovaFormaDePagamento, R.string.metodo_de_pagamento_vazio);
        if (mensagem_alerta == null){
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                ControleDatabase database = ControleDatabase.getDatabase(PagActivity.this);

                List<Pag> lista = database.PagDao().queryForForma(mensagem_alerta);

                if (modo == NOVO) {

                    if (lista.size() > 0){

                        PagActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilsGUI.avisoErro(PagActivity.this, R.string.forma_de_pagamento_ja_cadastrada);
                            }
                        });

                        return;
                    }

                    formadepagamento.setMensagem_alerta(mensagem_alerta);

                    database.PagDao().insert(formadepagamento);

                } else {

                    if (!mensagem_alerta.equals(formadepagamento.getMensagem_alerta())){

                        if (lista.size() >= 1){

                            PagActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsGUI.avisoErro(PagActivity.this, R.string.forma_de_pagamento_ja_cadastrada);
                                }
                            });

                            return;
                        }

                        formadepagamento.setMensagem_alerta(mensagem_alerta);

                        database.PagDao().update(formadepagamento);
                    }
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_pag, menu);
        return true;
    }

    public void limparCampos(){
        editTexNovaFormaDePagamento.setText(null);
        Toast.makeText(this, R.string.limpacampos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            case R.id.menuItemLimpar:
                limparCampos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}