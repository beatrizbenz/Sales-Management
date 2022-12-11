package com.example.Gestao_De_Brigadeiros;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Gestao_De_Brigadeiros.Model.Dados;
import com.example.Gestao_De_Brigadeiros.Model.Pag;
import com.example.Gestao_De_Brigadeiros.Persistencia.ControleDatabase;
import com.example.Gestao_De_Brigadeiros.Utils.UtilsDate;
import com.example.Gestao_De_Brigadeiros.Utils.UtilsGUI;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DadosActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //Inicialização das variaveis
    public  static  final  String MODO = "MODO";
    public static final String ID      = "ID";

    public static final int NOVO = 1;
    public static final int ALTERAR = 2;
    private int modo;

    private EditText editTextNome, editTextTelefone;
    private RadioGroup rgClassificacaoDoCliente;
    private CheckBox cbClienteEspecial;
    private Spinner sp_FormaDePagamento;
    private Dados dados;
    private List<Pag> listaFormadepagamentos;
    private Calendar DataOrdemGerada;
    private Calendar DataDoPagamento;
    private TextView textViewDataDoPagamento;
    private EditText editTextDataDoPagamento;
    private TextView textViewDataDaVenda;
    private EditText editTextDataDaVenda, editTextPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda);

        // Criando as variaves que guarda os endereços das instancias.

        // Text
        editTextNome = findViewById(R.id.editTextNome);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        textViewDataDoPagamento = findViewById(R.id.textViewDataDoPagamento);
        editTextDataDoPagamento = findViewById(R.id.editTextDataDoPagamento);
        textViewDataDaVenda = findViewById(R.id.textViewDataDaVenda);
        editTextDataDaVenda = findViewById(R.id.editTextDataDaVenda);
        editTextPagamento = findViewById(R.id.editTextPagamento);
        editTextDataDaVenda.setEnabled(false);

        //ComboBox de Classificacao do Cliente
        cbClienteEspecial = findViewById(R.id.cbClienteEspecial);
        Intent intent = getIntent();


        //Radion Group
        rgClassificacaoDoCliente = findViewById(R.id.rgClassificacaoDoCliente);

        //Spinner
        sp_FormaDePagamento = findViewById(R.id.sp_FormaDePagamento);

        //Inicianização da Interface
        final Bundle bundle = intent.getExtras();

        //Criando Tela Nova a partir da chave
        modo = bundle.getInt(MODO, NOVO);

        // Inicializando o calendario
        DataOrdemGerada = Calendar.getInstance();
        DataOrdemGerada.add(Calendar.YEAR,- getResources().getInteger(R.integer.anos_para_tras));

        //Pegando o registro setado
        DataDoPagamento = Calendar.getInstance();

        //Desabilitando o foco, ficando apenasa visivel na hora que clicar para setar os dados
        editTextDataDoPagamento.setFocusable(false);
        editTextDataDoPagamento.setOnClickListener(new View.OnClickListener() {

            //Tratando os eventos do click do calendario
            @Override
            public void onClick(View v) {
                        DatePickerDialog picker = new DatePickerDialog(DadosActivity.this, R.style.CustomDatePickerDialogTheme, DadosActivity.this,
                                DataDoPagamento.get(Calendar.YEAR),
                                DataDoPagamento.get(Calendar.MONTH),
                                DataDoPagamento.get(Calendar.DAY_OF_MONTH));

                picker.show();
            }
        });

        //Metodo para crregar a forma de pagamento na tela
        carregaFormaDePagamento();

        //Condição ao clicar no editar para substituir os dados
        if (modo == ALTERAR){

            editTextNome.requestFocus();
            setTitle(getString(R.string.cadastro_alterar));

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    ControleDatabase database = ControleDatabase.getDatabase(DadosActivity.this);

                    dados = database.DadosDAO().queryForId(id);

                    //Salvando os dados editados no banco
                    DadosActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Substituindo os dados
                            editTextNome.setText(dados.getNome());
                            editTextTelefone.setText(String.valueOf(dados.getTelefone()));
                            editTextPagamento.setText(String.valueOf(dados.getValor()));

                            DataDoPagamento.setTime(dados.getDataOrdemGerada());
                            String textoData = UtilsDate.formatDate(DadosActivity.this, dados.getDataOrdemGerada());

                            editTextDataDoPagamento.setText(textoData);
                            textoData = UtilsDate.formatDate(DadosActivity.this, dados.getDataOrdemGerada());


                            editTextDataDaVenda.setText(textoData);
                            int classificacao = dados.getClassificacao();

                            RadioButton button;
                            switch (classificacao) {
                                case Dados.ALUNO:
                                    button = findViewById(R.id.rbAluno);
                                    button.setChecked(true);
                                    break;

                                case Dados.FUNCIONARIO:
                                    button = findViewById(R.id.rbFuncionario);
                                    button.setChecked(true);
                                    break;
                                case Dados.COMUNIDADE:
                                    button = findViewById(R.id.rbComunidadeExterna);
                                    button.setChecked(true);
                                    break;
                            }
                            cbClienteEspecial.setChecked(dados.isCliente_especial());

                            int posicao = posicaoFormaDePagamento(dados.getId_forma_pagamento());
                            sp_FormaDePagamento.setSelection(posicao);
                       }
                   });
                }
           });

        }else{

            //Setando o nome da tela
            setTitle(getString(R.string.nova_ordem_venda));

            //Trazendo os campos limpos para preencher do 0
            dados = new Dados("");
            textViewDataDaVenda.setVisibility(View.INVISIBLE);
            editTextDataDaVenda.setVisibility(View.INVISIBLE);
        }

    }


    //Inflando os menus para aparecer na tela
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes,menu);
        return true;
    }

    //Cancelando a seção
    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    //Método validar e salvar no banco
    public void salvar() {

        String nome = UtilsGUI.validaCampoTexto(this, editTextNome, R.string.nome_vazio);

        if (nome == null) {
            return;
        }
        String telefone = UtilsGUI.validaCampoTexto(this, editTextTelefone, R.string.telefone_vazio);

        if (telefone == null || telefone.trim().isEmpty()) {
            return;
        }

        String Valor = UtilsGUI.validaCampoTexto(this, editTextPagamento, R.string.valor_vazio);
        if (Valor == null){
            return;
        }

        String txtData = UtilsGUI.validaCampoTexto(this, editTextDataDoPagamento, R.string.data_pagamento_vazia);
        if (txtData == null){
            return;
        }

      int tipo;

        switch (rgClassificacaoDoCliente.getCheckedRadioButtonId()) {
            case R.id.rbAluno:
                tipo = Dados.ALUNO;
                break;
            case R.id.rbFuncionario:
                tipo = Dados.FUNCIONARIO;
                break;
            case R.id.rbComunidadeExterna:
                tipo = Dados.COMUNIDADE;
                break;
            default:
                tipo = -1;
        }

        if(tipo ==-1){
            return;

        }


        boolean cliente = cbClienteEspecial.isChecked();

        Pag formadepagamento = (Pag) sp_FormaDePagamento.getSelectedItem();

        if (formadepagamento != null){

            dados.setId_forma_pagamento(formadepagamento.getId());
        }

        dados.setNome(nome);
        dados.setTelefone(telefone);
        dados.setClassificacao(tipo);
        dados.setCliente_especial(cliente);
        dados.setDataDePagamento(DataDoPagamento.getTime());
        dados.setValor(Valor);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                ControleDatabase database = ControleDatabase.getDatabase(DadosActivity.this);

                if (modo == NOVO) {

                    dados.setDataOrdemGerada(new Date());

                    database.DadosDAO().insert(dados);

                } else {

                    database.DadosDAO().update(dados);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    //Trazendo pela posição a forma de pagamento ordenada
    private int posicaoFormaDePagamento(int formaId){

        for (int pos = 0; pos < listaFormadepagamentos.size(); pos++){

            Pag formas = listaFormadepagamentos.get(pos);

            if (formas.getId() == formaId){
                return pos;
            }
        }

        return -1;
    }

    //Carregando a Forma de Pagamento pelo Spn
    private void carregaFormaDePagamento(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ControleDatabase database = ControleDatabase.getDatabase(DadosActivity.this);

                listaFormadepagamentos = database.PagDao().queryAll();

                DadosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Pag> spinnerAdapter = new ArrayAdapter<>(DadosActivity.this, android.R.layout.simple_list_item_1, listaFormadepagamentos);

                        sp_FormaDePagamento.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    public static void novoCadastro(Activity activity, int requestCode){

        Intent intent = new Intent(activity, DadosActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent,requestCode);
    }

    public static void alterarCadastro(Activity activity, int requestCode, Dados dados){

        Intent intent = new Intent(activity, DadosActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, dados.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        DataDoPagamento.set(year, month, dayOfMonth);

        String textoData = UtilsDate.formatDate(this, DataDoPagamento.getTime());

        editTextDataDoPagamento.setText(textoData);
    }

    @Override
    public void onBackPressed(){
        cancelar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}