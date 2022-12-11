package com.example.Gestao_De_Brigadeiros;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Gestao_De_Brigadeiros.Model.Pag;
import com.example.Gestao_De_Brigadeiros.Persistencia.ControleDatabase;
import com.example.Gestao_De_Brigadeiros.Utils.UtilsGUI;

import java.util.List;

public class PagamentoActivity extends AppCompatActivity {

    private static final int REQUEST_NOVA_FORMA_DE_PAGAMENTO = 1;
    private static final int REQUEST_ALTERAR_FORMA_DE_PAGAMENTO = 2;

    private ListView listViewFormasDePagamento;
    private ArrayAdapter<Pag> listaAdapter;
    private List<Pag> lista;
    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, PagamentoActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewFormasDePagamento = findViewById(R.id.listViewFormasDePagamento);

        listViewFormasDePagamento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pag tipo = (Pag) parent.getItemAtPosition(position);

                PagActivity.alterar(PagamentoActivity.this, REQUEST_ALTERAR_FORMA_DE_PAGAMENTO, tipo);
            }
        });

        carregaCampus();

        registerForContextMenu(listViewFormasDePagamento);

        setTitle(getString(R.string.forma_de_pagamento));
    }

    private void carregaCampus(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

               ControleDatabase database = ControleDatabase.getDatabase(PagamentoActivity.this);

                lista = database.PagDao().queryAll();

                PagamentoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(PagamentoActivity.this, android.R.layout.simple_list_item_1, lista);

                        listViewFormasDePagamento.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void verificaFormaDePagamento(final Pag formadepagamento){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ControleDatabase database = ControleDatabase.getDatabase(PagamentoActivity.this);

                int lista = database.DadosDAO().queryForFormapagId(formadepagamento.getId());

                if (lista > 0){

                    PagamentoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.avisoErro(PagamentoActivity.this, R.string.forma_de_pagamento_em_uso);
                        }
                    });

                    return;
                }

                PagamentoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excluirFormaDePagamento(formadepagamento);
                    }
                });
            }
        });
    }

    private void excluirFormaDePagamento(final Pag formadepagamento){

        String mensagem = getString(R.string.deseja_apagar) + "\n" + formadepagamento.getMensagem_alerta();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        ControleDatabase database = ControleDatabase.getDatabase(PagamentoActivity.this);

                                        database.PagDao().delete(formadepagamento);

                                        PagamentoActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(formadepagamento);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_NOVA_FORMA_DE_PAGAMENTO || requestCode == REQUEST_ALTERAR_FORMA_DE_PAGAMENTO) && resultCode == Activity.RESULT_OK) {

            carregaCampus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opcoes_de_pagamento, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                PagActivity.novo(this, REQUEST_NOVA_FORMA_DE_PAGAMENTO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        final Pag formadepagamento = (Pag) listViewFormasDePagamento.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemEditar:
                PagActivity.alterar(this, REQUEST_ALTERAR_FORMA_DE_PAGAMENTO, formadepagamento);
                return true;

            case R.id.menuItemApagar:
                verificaFormaDePagamento(formadepagamento);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}