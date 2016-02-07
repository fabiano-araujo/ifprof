package com.example.fabiano.ifprof;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.fabiano.ifprof.model.Prova;

public class AlternativasMarcadas extends AppCompatActivity {
    private RadioButton rbA;
    private RadioButton rbB;
    private RadioButton rbC;
    private RadioButton rbD;
    private RadioButton rbE;
    private RadioGroup rgAlternativas;
    private TextView txtPergunta;
    private TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternativas_marcadas);
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        try{
            Prova prova = getIntent().getExtras().getParcelable("prova");
            txtTitle.setText("Prova de "+prova.getDisciplina().getNomeDisciplina()+" para a turma "+prova.getAvaliacao().getTurma().getNomeTurma());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
