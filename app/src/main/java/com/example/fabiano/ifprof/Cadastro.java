package com.example.fabiano.ifprof;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.model.Professor;


public class Cadastro extends AppCompatActivity {
    private LinearLayout llMainCadastro;
    private Toolbar tbCadastro;
    private EditText edtEmailCd;
    private EditText edtSenha;
    private EditText edtNomeProfessorCd;
    private EditText edtMatriculaCd;
    private TextInputLayout iptSenha;
    private TextInputLayout iptNomeProfessorCd;
    private TextInputLayout iptMatriculaCd;
    private TextInputLayout iptEmailcd;

    private SwitchCompat switchCompatCd;
    private Button btnCadastrarCd;
    private Professor professor;
    private Repositorio repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        instance();
        setSupportActionBar(tbCadastro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        professor = new Professor();
        btnCadastrarCd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filled()) {
                    professor.setNomeProfessor(edtNomeProfessorCd.getText().toString().trim());
                    professor.setMatricula(edtMatriculaCd.getText().toString());
                    professor.setEmail(edtEmailCd.getText().toString());
                    professor.setSenha(edtSenha.getText().toString());
                    professor.setOnline("on");

                    try {
                        repositorio = new Repositorio(Cadastro.this);
                        boolean save = repositorio.insert(professor);
                        repositorio.close();
                        if (save) {
                            Intent intent = new Intent(Cadastro.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                            startActivity(new Intent(Cadastro.this, SetPhoto.class));
                            Cadastro.this.finish();
                        } else {
                            snackbar("A matrícula " + professor.getMatricula() + " Já existe!");
                        }
                    } catch (Exception e) {
                        AlertDialog.Builder x = new AlertDialog.Builder(Cadastro.this);
                        x.setMessage(e.getMessage()).setNeutralButton("ok", null).show();
                    }

                } else {
                    Snackbar.make(llMainCadastro, "Informe todos os dados corretamente!", Snackbar.LENGTH_LONG).setAction("Ajuda", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder x = new AlertDialog.Builder(Cadastro.this);
                            x.setMessage("Ajuda").setNeutralButton("ok", null).show();
                        }
                    }).show();
                }
            }
        });
        edtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtSenha.getText().toString().length() > 0) {
                    switchCompatCd.setVisibility(View.VISIBLE);
                } else {
                    switchCompatCd.setVisibility(View.GONE);
                }
            }
        });
        switchCompatCd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    edtSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    edtSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        edtNomeProfessorCd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        edtMatriculaCd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edtSenha.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
    }
    public void instance(){
        llMainCadastro = (LinearLayout)findViewById(R.id.llMainCadastro);
        edtNomeProfessorCd = (EditText)findViewById(R.id.edtNomeProfessorCd);
        edtSenha = (EditText)findViewById(R.id.edtSenhaCd);
        switchCompatCd = (SwitchCompat)findViewById(R.id.switcherCd);
        tbCadastro = (Toolbar)findViewById(R.id.tbcadastro);
        btnCadastrarCd = (Button)findViewById(R.id.btnCadastrarCd);
        edtMatriculaCd = (EditText)findViewById(R.id.edtMatriculaCd);
        iptMatriculaCd = (TextInputLayout)findViewById(R.id.iptMatriculaCd);
        iptEmailcd = (TextInputLayout)findViewById(R.id.iptEmailCd);
        iptSenha = (TextInputLayout)findViewById(R.id.iptSenhaCd);
        edtEmailCd = (EditText)findViewById(R.id.edtEmailCd);
        iptNomeProfessorCd = (TextInputLayout)findViewById(R.id.iptNomeProfessorCd);
    }
    public void snackbar(String mensage ){
        Snackbar.make(llMainCadastro,mensage,Snackbar.LENGTH_LONG).setAction("Ajuda", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder x = new AlertDialog.Builder(Cadastro.this);
                x.setMessage("Ajuda").setNeutralButton("ok", null).show();
            }
        }).show();
    }
    public boolean filled(){
        boolean checkName = AlertsAndControl.check(iptNomeProfessorCd, edtNomeProfessorCd, 1, "Digite seu nome!");
        boolean checkMatricula = AlertsAndControl.check(iptMatriculaCd,edtMatriculaCd,4,"No mínimo 4 dígitos!");
        boolean checkEmail = AlertsAndControl.check(iptEmailcd,edtEmailCd,1,"Digite seu email!");
        boolean isValid = AlertsAndControl.isValidEmailAddress(edtEmailCd,iptEmailcd);
        if(checkName && checkMatricula && checkEmail&& checkSenha()&&isValid){
            return true;
        }else{
            return false;
        }
    }
    public boolean checkSenha(){
        boolean isValid = true;
        if(edtSenha.getText().toString().trim().length() < 4){
            iptSenha.setError("No mínimo 4 dígitos!");
            edtSenha.requestFocus();
            isValid = false;
        }else{
            iptSenha.setErrorEnabled(false);
        }
        return isValid;
    }

}
