package com.developer.fabiano.ifprof;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Progress;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Prova;
import com.developer.fabiano.ifprof.model.Questao;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class ActivityProva extends AppCompatActivity {
    private LinearLayout llProva;
    private Float valor;
    private EditText edtValor;
    private Toolbar tbProvas;
    private Button btnGerarProva;
    private String saveAt;
    private Prova prova;
    double nota = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_prova);
        btnGerarProva = (Button)findViewById(R.id.btnGerarProva);
        llProva = (LinearLayout)findViewById(R.id.llProva);
        tbProvas = (Toolbar)findViewById(R.id.tbProvas);
        setSupportActionBar(tbProvas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prova = getIntent().getExtras().getParcelable("prova");
        Repositorio repositorio = new Repositorio(ActivityProva.this);
        prova.setProfessor(repositorio.getLogged());
        repositorio.close();
        final Avaliacao avaliacao = prova.getAvaliacao();
        final List<Questao> questaoList = avaliacao.getQuestoes();
        final TextInputLayout textInputLayoutArray[] = new TextInputLayout[questaoList.size()];
        final EditText editTextList[] = new EditText[questaoList.size()];
        valor = Float.parseFloat(avaliacao.getValor());
        for (int i = 0; i <questaoList.size() ; i++) {
            final Questao questao = questaoList.get(i);
            questao.setPosition(i);
            View view = getLayoutInflater().inflate(R.layout.item_show_questoes, null);
            TextView txtPergunta = (TextView)view.findViewById(R.id.txtPergunta);
            TextView txtAlternativa = (TextView)view.findViewById(R.id.txtAlternativaCorreta);
            TextView letra = (TextView)view.findViewById(R.id.txtLetra);
            View linha = (View)view.findViewById(R.id.view);
            linha.setVisibility(View.GONE);
            TextInputLayout textInputLayout = (TextInputLayout)view.findViewById(R.id.iptValor);
            textInputLayoutArray[i] = textInputLayout;
            letra.setVisibility(View.GONE);
            edtValor = (EditText)view.findViewById(R.id.edtValor);
            editTextList[i] = edtValor;

            if (questao.getAlternativaCorreta().equals(" a)")){
                txtAlternativa.setText(questao.getAlternativaCorreta()+" " + questao.getAlternativaA());
            }else if (questao.getAlternativaCorreta().equals(" b)")){
                txtAlternativa.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaB());
            }else if (questao.getAlternativaCorreta().equals(" c)")){
                txtAlternativa.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaC());
            }else if (questao.getAlternativaCorreta().equals(" d)")){
                txtAlternativa.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaD());
            }else if (questao.getAlternativaCorreta().equals(" e)")) {
                txtAlternativa.setText(questao.getAlternativaCorreta() + " " + questao.getAlternativaE());
            }
            txtPergunta.setText(questao.getPergunta());
            textInputLayout.setVisibility(View.VISIBLE);

            double numero= valor / questaoList.size();
            DecimalFormat formato = new DecimalFormat("#.##");
            questao.setValor(formato.format(numero).replace(",","."));
            edtValor.setText(questao.getValor());

            edtValor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        float valueChanged = Float.parseFloat(s.toString());
                        Float soma = 0f;
                        for (int i = 0; i < questaoList.size(); i++) {
                            String values = editTextList[i].getText().toString().trim();
                            if (values.length() > 0 && i != questao.getPosition()) {
                                soma += Float.parseFloat(values);
                            }
                        }
                        if (soma + valueChanged > valor) {
                            float disponivel = valor - (soma);
                            DecimalFormat formato = new DecimalFormat("#.##");
                            questao.setValor(formato.format(disponivel).replace(",", "."));
                            AlertsAndControl.alert(ActivityProva.this, "A soma dos valores de cada questão passou do valor da prova que é " + avaliacao.getValor() + ". Diminua o valor de outra e tente novamente.(Valor disponível " + questao.getValor() + ")", "Aviso");
                            editTextList[questao.getPosition()].setText(questao.getValor());
                        }
                    } catch (NumberFormatException e) {}
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            llProva.addView(view);
        }
        btnGerarProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 0;
                boolean isValid = true;
                for (int i = 0; i < questaoList.size(); i++) {
                    if (editTextList[i].getText().toString().trim().length() == 0){
                        textInputLayoutArray[i].setError("digite o valor da questão!");
                        editTextList[i].requestFocus();
                        isValid = false;
                        break;
                    }else{
                        nota += Double.parseDouble(editTextList[i].getText().toString().trim());
                        textInputLayoutArray[i].setErrorEnabled(false);
                    }
                }
                if (isValid){
                    nota = AlertsAndControl.round(nota,1);
                    if (nota < Double.parseDouble(prova.getAvaliacao().getValor())){
                        AlertDialog.Builder notaMenor = new AlertDialog.Builder(ActivityProva.this);
                        notaMenor.setCancelable(false);
                        notaMenor.setMessage("A soma das notas foi menor do que o valor da prova, mudar o valor para "+nota+"?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prova.getAvaliacao().setValor(nota+"");
                                try {
                                    Repositorio repositorio = new Repositorio(ActivityProva.this);
                                    repositorio.insertProva(avaliacao);
                                    repositorio.close();
                                    existFile();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Não",null).setTitle("valor da prova").show();
                    }else{
                        try {
                            Repositorio repositorio = new Repositorio(ActivityProva.this);
                            prova.setCode(repositorio.insertProva(avaliacao));
                            repositorio.close();
                            existFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        }

    public String existFile(){
        String file = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+prova.getProfessor().getNomeProfessor()+"/"+prova.getDisciplina().getNomeDisciplina()+"/provas/"+prova.getAvaliacao().getAssunto()+"-"+prova.getAvaliacao().getTurma().getNomeTurma()+".pdf";
        final File doc = new File(file);
        if (doc.exists()){
            AlertDialog.Builder exists = new AlertDialog.Builder(ActivityProva.this);
            exists.setMessage("Você já tem essa prova salva, excluir a anterior?").setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doc.delete();
                    try {
                        Progress criarProva = new Progress(ActivityProva.this,prova,0);
                        criarProva.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton("Manter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 1; true; i++) {
                        String file = Environment.getExternalStorageDirectory().getPath() + "/Ifprof/" + prova.getProfessor().getNomeProfessor() + "/" + prova.getDisciplina().getNomeDisciplina() + "/provas/" + prova.getAvaliacao().getAssunto() + "-" + prova.getAvaliacao().getTurma().getNomeTurma() + "(" + i + ").docx";
                        File exists = new File(file);
                        if (!exists.exists()) {
                            try {
                                Progress criarProva = new Progress(ActivityProva.this,prova,i);
                                criarProva.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }).setTitle("Arquivo já existe").show();
        }else{
            try {
                Progress criarProva = new Progress(ActivityProva.this,prova,0);
                criarProva.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return saveAt;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertsAndControl.alert(this, "Voçê tem que informar os valores das questões!", "Aviso");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234){
            startActivity(new Intent(ActivityProva.this,ShowAvaliacoes.class));
            ActivityProva.this.finish();
        }
    }
}

