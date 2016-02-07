package com.example.fabiano.ifprof;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
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

public class Login extends AppCompatActivity {
    private EditText edtMatricula;
    private EditText edtSenha;
    private TextInputLayout iptMatricula;
    private TextInputLayout iptsenha;
    private Button btnCadastrar;
    private Button btnEntrar;
    private Button btnList;
    private Repositorio repositorio;
    private SwitchCompat switchCompat;
    private Toolbar tbLogin;
    private LinearLayout llLogin;
    public Context context;
    private AppCompatCheckBox saveLogin;
    private static final String SHOW = "Show,";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance();
        try{
            repositorio = new Repositorio(this);
            Professor professor = repositorio.showed();
            edtMatricula.setText(professor.getMatricula());
            edtSenha.setText(professor.getSenha());
            if (professor.getMatricula() != null){
                switchCompat.setVisibility(View.VISIBLE);
            }
        }catch(SQLiteException e){
           AlertsAndControl.alert(this, e.getMessage(),"Erro");
        }
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkMatricula() && checkSenha()){
                        String typeLogin;
                        if(saveLogin.isChecked()){
                            typeLogin = "yes";
                        }else{
                            typeLogin = "no";
                        }
                        if(repositorio.login(edtMatricula.getText().toString().trim(), edtSenha.getText().toString(),typeLogin)){
                            repositorio.close();
                            startActivity(new Intent(Login.this,MenuMain.class));
                            Login.this.finish();
                        }else{
                            Snackbar.make(llLogin,"Matrícula ou senha incorretos!",Snackbar.LENGTH_LONG).setAction("Esqueceu sua senha?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertsAndControl.alert(Login.this, "ok","Ajuda");
                                }
                            }).show();
                        }
                    }else{
                        Snackbar.make(llLogin,"Informe os dados corretamente!",Snackbar.LENGTH_LONG).setAction("Esqueceu sua senha?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertsAndControl.alert(Login.this, "ok","Ajuda");
                            }
                        }).show();
                    }
                }catch (Exception e){
                    AlertsAndControl.alert(Login.this,e.getMessage(),"Erro");
                }
            }
        });
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repositorio.close();
                startActivity(new Intent(Login.this,Cadastro.class));
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
                    switchCompat.setVisibility(View.VISIBLE);
                } else {
                    switchCompat.setVisibility(View.GONE);
                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        edtMatricula.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        edtSenha.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        if (getIntent().getBooleanExtra("EXIT", false)) {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnSair) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public boolean checkSenha(){
        boolean isValid = true;
        if (edtSenha.getText().toString().trim().isEmpty()){
            iptsenha.setError("Digite sua senha!");
            requestFocus(edtSenha);
            isValid = false;
        }else{
            iptsenha.setErrorEnabled(false);
        }
        return isValid;
    }
    public boolean checkMatricula(){
        boolean isValid = true;
        if(edtMatricula.getText().toString().trim().isEmpty()){
            iptMatricula.setError("Digite sua matricula!");
            requestFocus(edtMatricula);
            isValid = false;
        }else{
            iptMatricula.setErrorEnabled(false);
        }
        return  isValid;
    }
    public void instance(){
        tbLogin = (Toolbar)findViewById(R.id.tbLogin);
        setSupportActionBar(tbLogin);
        switchCompat = (SwitchCompat)findViewById(R.id.switcher);
        edtMatricula = (EditText)findViewById(R.id.edtMatricula);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);
        btnEntrar = (Button)findViewById(R.id.btnEntrar);
        llLogin = (LinearLayout)findViewById(R.id.llLogin);
        iptMatricula = (TextInputLayout)findViewById(R.id.iptMatricula);
        iptsenha = (TextInputLayout)findViewById(R.id.iptSenha);
        saveLogin = (AppCompatCheckBox)findViewById(R.id.cbSaveLogin);
    }
}

