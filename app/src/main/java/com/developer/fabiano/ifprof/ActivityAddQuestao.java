package com.developer.fabiano.ifprof;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Alternativa;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Questao;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ActivityAddQuestao extends AppCompatActivity {
    private Toolbar tbProvas;
    private TextInputLayout iptA;
    private TextInputLayout iptB;
    private TextInputLayout iptC;
    private TextInputLayout iptD;
    private TextInputLayout iptE;
    private TextInputLayout iptPergunta;

    private EditText edtA;
    private EditText edtB;
    private EditText edtC;
    private EditText edtD;
    private EditText edtE;
    private EditText edtPergunta;

    private RadioButton rbA;
    private RadioButton rbB;
    private RadioButton rbC;
    private RadioButton rbD;
    private RadioButton rbE;
    private RadioGroup rgAlternativas;

    private Button btnAddQuestao;
    private Repositorio repositorio;
    private Professor professorLogged;
    private LinearLayout llAddQuestao;
    private Avaliacao avaliacao;
    private LinearLayout llAddImage;
    private Button btnAddImage;
    private ImageView ivAddImage;
    private Button btnExcluir;
    public static int IMAGEM_INTERNA = 1;
    private AppCompatSpinner spnDisciplina;

    private boolean alter = false;
    private String pathImg;

    private Questao questaoAlter;
    private ArrayAdapter<String> adtSpnDisciplina;
    List<Disciplina> disciplinaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questao);
        instance();
        setSupportActionBar(tbProvas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repositorio = new Repositorio(this);
        adtSpnDisciplina = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        adtSpnDisciplina.setDropDownViewResource(android.R.layout.simple_list_item_1);
        professorLogged = repositorio.getLogged();
        disciplinaList = repositorio.getDisciplinas(professorLogged, "");
        for (int i = 0;i < disciplinaList.size();i++){
            adtSpnDisciplina.add(disciplinaList.get(i).getNomeDisciplina());
        }
        if(disciplinaList.size() == 0){
            AlertsAndControl.noNeedData(ActivityAddQuestao.this, ActivityAddDisciplinas.class, "Para adicionar uma questão é necessário criar uma disciplina antes!",AlertsAndControl.DISCIPLINAS,null);
        }
        spnDisciplina.setAdapter(adtSpnDisciplina);
        repositorio.close();

        try{
            questaoAlter = getIntent().getExtras().getParcelable("alter");
            alter = true;
            edtPergunta.setText(questaoAlter.getPergunta());
            edtA.setText(questaoAlter.getAlternativaA());
            edtB.setText(questaoAlter.getAlternativaB());
            edtC.setText(questaoAlter.getAlternativaC());
            edtD.setText(questaoAlter.getAlternativaD());
            edtE.setText(questaoAlter.getAlternativaE());
            for (int i = 0; i < disciplinaList.size(); i++) {
                if (disciplinaList.get(i).getIdDisciplina() == questaoAlter.getIdDisciplina()){
                    spnDisciplina.setSelection(i);
                }
            }
            if(questaoAlter.getPathImage() != null){
                pathImg = questaoAlter.getPathImage();
                llAddImage.setVisibility(View.VISIBLE);
                btnAddImage.setVisibility(View.GONE);
                ivAddImage.setImageBitmap(ImageUtil.setPic(pathImg, ivAddImage.getWidth(), ivAddImage.getHeight()));
            }

            if (questaoAlter.getAlternativaCorreta().equals(" a)")){
                rbA.setChecked(true);
            }else if (questaoAlter.getAlternativaCorreta().equals(" b)")){
                rbB.setChecked(true);
            }else if (questaoAlter.getAlternativaCorreta().equals(" c)")){
                rbC.setChecked(true);
            }else if (questaoAlter.getAlternativaCorreta().equals(" d)")){
                rbD.setChecked(true);
            }else if (questaoAlter.getAlternativaCorreta().equals(" e)")){
                rbE.setChecked(true);
            }
            btnAddQuestao.setText("Alterar");
        }catch (NullPointerException e){
            alter = false;
        }
        try {
            if (questaoAlter == null){
                avaliacao = getIntent().getExtras().getParcelable("verQuestoes");
                alter = false;
            }
            if (avaliacao != null){
                for (int i = 0; i < disciplinaList.size(); i++) {
                    if (disciplinaList.get(i).getIdDisciplina() == avaliacao.getIdDisciplina()){
                        spnDisciplina.setSelection(i);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(savedInstanceState != null){
            pathImg = savedInstanceState.getString("path");
            if (pathImg != null){
                llAddImage.setVisibility(View.VISIBLE);
                btnAddImage.setVisibility(View.GONE);
                ivAddImage.setImageBitmap(ImageUtil.setPic(pathImg, ivAddImage.getWidth(), ivAddImage.getHeight()));
            }
        }
        btnAddQuestao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (AlertsAndControl.check(iptPergunta,edtPergunta,1,"Digite a pergunta!") && AlertsAndControl.check(iptA,edtA,1,"digite algo nessa alternativa!")&& AlertsAndControl.check(iptB,edtB,1,"digite algo nessa alternativa!")&& AlertsAndControl.check(iptC,edtC,1,"digite algo nessa alternativa!")&& AlertsAndControl.check(iptD,edtD,1,"digite algo nessa alternativa!")&& AlertsAndControl.check(iptE,edtE,1,"digite algo nessa alternativa!")){

                        RadioButton correta = (RadioButton)findViewById(rgAlternativas.getCheckedRadioButtonId());

                        Questao questao = new Questao(edtPergunta.getText().toString().trim(),
                                correta.getText().toString(),
                                edtA.getText().toString().trim(),
                                edtB.getText().toString().trim(),
                                edtC.getText().toString().trim(),
                                edtD.getText().toString().trim(),
                                edtE.getText().toString().trim());
                        questao.setIdDisciplina(disciplinaList.get(spnDisciplina.getSelectedItemPosition()).getIdDisciplina());
                        repositorio = new Repositorio(ActivityAddQuestao.this);
                        questao.setPathImage(pathImg);
                        questao.setIdProfessor(professorLogged.getId());
                        if (alternativasIguais()){
                            if (alter){
                                if (!questao.getPathImage().equals(questaoAlter.getPathImage())||!questao.getPergunta().equals(questaoAlter.getPergunta()) || !questao.getAlternativaCorreta().equals(questaoAlter.getAlternativaCorreta()) || !questao.getAlternativaA().equals(questaoAlter.getAlternativaA()) || !questao.getAlternativaB().equals(questaoAlter.getAlternativaB()) || !questao.getAlternativaC().equals(questaoAlter.getAlternativaC()) || !questao.getAlternativaD().equals(questaoAlter.getAlternativaD()) || !questao.getAlternativaE().equals(questaoAlter.getAlternativaE())){
                                    String dados = "'" + questao.getPergunta() + "', " + DataBase.ALTERNATIVACORRETA + " = '" + questao.getAlternativaCorreta() + "' , " + DataBase.ALTERNATIA + " = '" + questao.getAlternativaA() + "' , "+ DataBase.ALTERNATIB + " = '" + questao.getAlternativaB() + "' , " + DataBase.ALTERNATIC + " = '" + questao.getAlternativaC() + "' , "+ DataBase.ALTERNATID + " = '" + questao.getAlternativaD() + "' , "+ DataBase.ALTERNATIE + " = '" + questao.getAlternativaE() + "' , "+ DataBase.PATHIMAGE + " = '" + questao.getPathImage() + "'";
                                    repositorio.update(DataBase.TABLE_QUESTAO, DataBase.PERGUNTA, dados, DataBase.ID_QUESTAO, questaoAlter.getIdQuestao(), "");
                                    Intent it = new Intent(ActivityAddQuestao.this,ShowQuestoes.class);
                                    it.putExtra("insert", "alter");
                                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(it);
                                    repositorio.close();
                                    ActivityAddQuestao.this.finish();
                                }else{
                                    AlertsAndControl.snackBar(llAddQuestao,"Nada foi alterado!");
                                }
                            }else{
                                if (repositorio.insert(questao)) {
                                    if (getIntent().getStringExtra("new") == null) {
                                        Intent it = new Intent(ActivityAddQuestao.this, ShowQuestoes.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        if (avaliacao != null){
                                            it.putExtra("escolherQuestoes",avaliacao);
                                        }else {
                                            it.putExtra("insert", "ok");
                                        }
                                        startActivity(it);
                                    } else {
                                        AlertsAndControl.vericaClass(ActivityAddQuestao.this, getIntent().getStringExtra("new"));
                                    }
                                    repositorio.close();
                                    ActivityAddQuestao.this.finish();
                                }
                            }
                        }else{
                            AlertsAndControl.snackBar(llAddQuestao,"Alternativas iguais!");
                        }
                        repositorio.close();
                    }else{
                        AlertsAndControl.snackBar(llAddQuestao, "Informe todas as informações corretamente!");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentGalary();
            }
        });
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddImage.setVisibility(View.VISIBLE);
                llAddImage.setVisibility(View.GONE);
                pathImg = null;
            }
        });
        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentGalary();
            }
        });
    }
    public void intentGalary(){
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK).setType("image/*"), "Selecione uma imagem"), IMAGEM_INTERNA);
    }
    public void instance(){
        tbProvas = (Toolbar)findViewById(R.id.tbProvas);
        btnAddQuestao = (Button)findViewById(R.id.btnAddQuestao);

        iptA = (TextInputLayout)findViewById(R.id.iptA);
        iptB = (TextInputLayout)findViewById(R.id.iptB);
        iptC = (TextInputLayout)findViewById(R.id.iptC);
        iptD = (TextInputLayout)findViewById(R.id.iptD);
        iptE = (TextInputLayout)findViewById(R.id.iptE);
        iptPergunta = (TextInputLayout)findViewById(R.id.iptPergunta);

        edtA = (EditText)findViewById(R.id.edtA);
        edtB = (EditText)findViewById(R.id.edtB);
        edtC = (EditText)findViewById(R.id.edtC);
        edtD = (EditText)findViewById(R.id.edtD);
        edtE = (EditText)findViewById(R.id.edtE);
        edtPergunta = (EditText)findViewById(R.id.edtPergunta);

        rbA = (RadioButton)findViewById(R.id.rbA);
        rbB = (RadioButton)findViewById(R.id.rbB);
        rbC = (RadioButton)findViewById(R.id.rbC);
        rbD = (RadioButton)findViewById(R.id.rbD);
        rbE = (RadioButton)findViewById(R.id.rbE);
        rgAlternativas = (RadioGroup)findViewById(R.id.rgAlternativas);

        llAddQuestao = (LinearLayout)findViewById(R.id.llAddQuestao);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnExcluir = (Button)findViewById(R.id.btnExcluir);
        llAddImage = (LinearLayout)findViewById(R.id.llAddImage);
        ivAddImage = (ImageView)findViewById(R.id.ivAddImage);

        spnDisciplina = (AppCompatSpinner)findViewById(R.id.spnDisciplina);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        try {
            if (resultCode == RESULT_OK){
                if(requestCode == IMAGEM_INTERNA){
                    pathImg = ImageUtil.getRealPathFromURI(this,intent.getData());;
                    ivAddImage.setImageBitmap(ImageUtil.setPic(pathImg,ivAddImage.getWidth(),ivAddImage.getHeight()));
                    llAddImage.setVisibility(View.VISIBLE);
                    btnAddImage.setVisibility(View.GONE);
                    btnExcluir.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            Snackbar.make(llAddQuestao, "Ocorreu um erro, escolha outro app para abrir a imagem ou tente novamente!", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
            llAddImage.setVisibility(View.GONE);
            btnAddImage.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (avaliacao != null){
            Intent it = new Intent(this,ShowQuestoes.class);
            it.putExtra("escolherQuestoes", avaliacao);
            startActivity(it);
            this.finish();
        }else{
            try{
                if (questaoAlter.getIntent() == 1){
                    super.onBackPressed();
                }
            }catch (NullPointerException e){
                startActivity(new Intent(this,ShowQuestoes.class));
                this.finish();
            }
        }
    }
    public boolean alternativasIguais(){
        String alert = "Igual a alternativa ";
        boolean isValid = true;
        String alternativaA = edtA.getText().toString();
        String alternativaB = edtB.getText().toString();
        String alternativaC = edtC.getText().toString();
        String alternativaD = edtD.getText().toString();
        String alternativaE = edtE.getText().toString();
        List<Alternativa> alternativas = new ArrayList<>();

        Alternativa al1 = new Alternativa();
        al1.setTexto(alternativaA);
        al1.setEditText(edtA);
        al1.setTextInputLayout(iptA);
        al1.setAlternava("A");
        alternativas.add(al1);

        Alternativa al2 = new Alternativa();
        al2.setTexto(alternativaB);
        al2.setEditText(edtB);
        al2.setTextInputLayout(iptB);
        al2.setAlternava("B");
        alternativas.add(al2);

        Alternativa al3 = new Alternativa();
        al3.setTexto(alternativaC);
        al3.setEditText(edtC);
        al3.setTextInputLayout(iptC);
        al3.setAlternava("C");
        alternativas.add(al3);

        Alternativa al4 = new Alternativa();
        al4.setTexto(alternativaD);
        al4.setEditText(edtD);
        al4.setTextInputLayout(iptD);
        al4.setAlternava("D");
        alternativas.add(al4);

        Alternativa al5 = new Alternativa();
        al5.setTexto(alternativaE);
        al5.setEditText(edtE);
        al5.setTextInputLayout(iptE);
        al5.setAlternava("E");
        alternativas.add(al5);

        for (int i = 0; i < alternativas.size() ; i++) {
            for (int j = 0; j <alternativas.size(); j++) {
                if (i != j){
                    Alternativa alternativa = alternativas.get(i);
                    Alternativa alternativa2 = alternativas.get(j);
                    if (alternativa.getTexto().equals(alternativa2.getTexto())){
                        alternativa.getTextInputLayout().setError(alert + alternativa2.getAlternava());
                        alternativa2.getTextInputLayout().setError(alert + alternativa.getAlternava());
                        alternativa.getEditText().requestFocus();
                        return false;
                    }else{
                        alternativa.getTextInputLayout().setErrorEnabled(false);
                        alternativa2.getTextInputLayout().setErrorEnabled(false);
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("path",pathImg);
        super.onSaveInstanceState(outState);
    }
}

