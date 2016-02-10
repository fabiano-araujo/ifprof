package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Questao;

public class VerQuestao extends AppCompatActivity {
    private TextView txtVerPergunta;
    private TextView txtAlternativaCorreta;
    private TextView txtAlternativaA;
    private TextView txtAlternativaB;
    private TextView txtAlternativaC;
    private TextView txtAlternativaD;
    private TextView txtAlternativaE;
    private Button btnEditarQuestao;
    private Toolbar tbVerQuestao;
    private ImageView ivImage;
    private Questao questao;
    private TextView txtDisciplina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_questao);

        tbVerQuestao = (Toolbar)findViewById(R.id.tbVerQuestao);
        setSupportActionBar(tbVerQuestao);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtVerPergunta = (TextView)findViewById(R.id.txtVerPergunta);
        txtAlternativaA = (TextView)findViewById(R.id.txtAlternativaA);
        txtAlternativaB = (TextView)findViewById(R.id.txtAlternativaB);
        txtAlternativaC = (TextView)findViewById(R.id.txtAlternativaC);
        txtAlternativaD = (TextView)findViewById(R.id.txtAlternativaD);
        txtAlternativaE = (TextView)findViewById(R.id.txtAlternativaE);
        txtAlternativaCorreta = (TextView)findViewById(R.id.txtAlternativaCorreta);
        btnEditarQuestao = (Button)findViewById(R.id.btnEditarQuestao);
        txtDisciplina = (TextView)findViewById(R.id.txtDisciplina);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        try{
            questao = getIntent().getExtras().getParcelable("verQuestao");
            txtVerPergunta.setText(questao.getPergunta());
            if (questao.getPathImage() != null){
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageBitmap(ImageUtil.setPic(Uri.parse(questao.getPathImage()), ivImage.getWidth(), ivImage.getHeight()));
            }
            txtAlternativaCorreta.setText(txtAlternativaCorreta.getText()+" "+questao.getAlternativaCorreta());
            txtAlternativaA.setText(" a) "+questao.getAlternativaA());
            txtAlternativaB.setText(" b) "+questao.getAlternativaB());
            txtAlternativaC.setText(" c) "+questao.getAlternativaC());
            txtAlternativaD.setText(" d) "+questao.getAlternativaD());
            txtAlternativaE.setText(" e) "+questao.getAlternativaE());
            Repositorio repositorio = new Repositorio(this);
            Disciplina disciplina = repositorio.getDisciplinas(repositorio.getLogged(),"and "+ DataBase.ID_DISCIPLINA+" = "+questao.getIdDisciplina()).get(0);
            txtDisciplina.setText(disciplina.getNomeDisciplina());
            repositorio.close();
            if (questao.getAlternativaCorreta().equals(" a)")){
                txtAlternativaA.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else if (questao.getAlternativaCorreta().equals(" b)")){
                txtAlternativaB.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else if (questao.getAlternativaCorreta().equals(" c)")){
                txtAlternativaC.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else if (questao.getAlternativaCorreta().equals(" d)")){
                txtAlternativaD.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else if (questao.getAlternativaCorreta().equals(" e)")){
                txtAlternativaE.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            btnEditarQuestao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(VerQuestao.this, ActivityAddQuestao.class);
                    questao.setIntent(1);
                    it.putExtra("alter",questao);
                    startActivity(it);
                }
            });
        }catch (Exception e){
            AlertsAndControl.alert(this,"Ocorreu algum erro ou não existe questões para visualizar! "+questao.getIdDisciplina(),"Aviso");
            e.printStackTrace();
            this.finish();
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

}
