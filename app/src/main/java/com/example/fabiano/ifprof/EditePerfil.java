package com.example.fabiano.ifprof;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.ImageUtil;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Professor;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditePerfil extends AppCompatActivity {
    private Professor professorPerfil;
    private Toolbar tbEditePerfil;
    private TextInputLayout iptNomeProfessorEdite;
    private EditText edtNomeProfessorEdite;
    private TextInputLayout iptMatriculaProfessorEdite;
    private EditText edtMatriculaProfessorEdite;
    private CircleImageView ivEditePerfil;
    private Button btnConcluirEdite;
    private LinearLayout llEditePerfil;
    public static final int IMAGEM_INTERNA = 12;
    private Repositorio repositorio;
    private EditText edtSenhaAntiga;
    private EditText edtNovaSenha;
    private TextInputLayout iptSenhaAntiga;
    private TextInputLayout iptNovaSenha;
    private Button btnEditarSenha;
    private LinearLayout llEditarSenha;
    private SwitchCompat strEditarSenha;
    private TextInputLayout iptEmail;
    private EditText edtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_perfil);
        instance();
        professorPerfil = getIntent().getExtras().getParcelable("professor");
        edtNomeProfessorEdite.setText(professorPerfil.getNomeProfessor().trim());
        edtMatriculaProfessorEdite.setText(professorPerfil.getMatricula());
        edtEmail.setText(professorPerfil.getEmail());
        if (!professorPerfil.getUriFoto().equals("null")) {
            ivEditePerfil.setImageBitmap(ImageUtil.setPic(Uri.parse(professorPerfil.getUriFoto()), ivEditePerfil.getWidth(), ivEditePerfil.getHeight()));
        }
        ivEditePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGEM_INTERNA);

            }
        });
        btnEditarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llEditarSenha.getVisibility() == View.GONE){
                    llEditarSenha.setVisibility(View.VISIBLE);
                    btnEditarSenha.setText("Deixar senha antiga");
                }else{
                    llEditarSenha.setVisibility(View.GONE);
                    btnEditarSenha.setText("Alterar senha");
                }
            }
        });
        btnConcluirEdite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (AlertsAndControl.check(iptNomeProfessorEdite,edtNomeProfessorEdite,1,"Digite seu nome!")&&AlertsAndControl.check(iptMatriculaProfessorEdite,edtMatriculaProfessorEdite,4,"No mínimo 4 dígitos!")&&AlertsAndControl.check(iptEmail,edtEmail,1,"Digite seu email!")&&AlertsAndControl.isValidEmailAddress(edtEmail, iptEmail)){
                        repositorio = new Repositorio(EditePerfil.this);
                        String nome = edtNomeProfessorEdite.getText().toString().trim();
                        String matricula = edtMatriculaProfessorEdite.getText().toString();
                        String email = edtEmail.getText().toString();
                        boolean isValid = true;
                        if (llEditarSenha.getVisibility() == View.VISIBLE){
                            if(!edtSenhaAntiga.getText().toString().trim().equals(professorPerfil.getSenha())){
                                iptSenhaAntiga.setError("Essa senha não é igual a sua senha antiga!");
                                edtSenhaAntiga.requestFocus();
                                isValid = false;
                            }else{
                                iptSenhaAntiga.setErrorEnabled(false);
                                if (AlertsAndControl.check(iptNovaSenha,edtNovaSenha,4,"No mínimo 4 dígitos!")){
                                    String dados = "'" + professorPerfil.getUriFoto() + "' ," + DataBase.NOME_PROFESSOR + " = '" + nome + "', " + DataBase.MATRICULA_PROFESSOR + " = " + matricula+ " , " + DataBase.SENHA + " = '" + edtNovaSenha.getText().toString()+"', " + DataBase.EMAIL + " = '" + email+"'";
                                    repositorio.update(DataBase.TABLE_PROFESSOR, DataBase.URIFOTO, dados, DataBase.ID_PROFESSOR, professorPerfil.getId(), "");
                                    backPerfil();
                                }
                            }
                        }else{
                            String dados = "'" + professorPerfil.getUriFoto() + "' ," + DataBase.NOME_PROFESSOR + " = '" + nome + "', " + DataBase.MATRICULA_PROFESSOR + " = " + matricula+" , "+ DataBase.EMAIL + " = '" + email+"'";
                            repositorio.update(DataBase.TABLE_PROFESSOR, DataBase.URIFOTO, dados, DataBase.ID_PROFESSOR, professorPerfil.getId(), "");
                            backPerfil();
                        }
                        repositorio.close();
                    }
                } catch (SQLiteException e) {
                    AlertDialog.Builder x = new AlertDialog.Builder(EditePerfil.this);
                    x.setMessage(e.getMessage()).setNeutralButton("ok", null).show();
                }
            }
        });
        edtSenhaAntiga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (edtSenhaAntiga.getText().toString().length() > 0) {
                    strEditarSenha.setVisibility(View.VISIBLE);
                } else if (edtNovaSenha.getText().toString().length() == 0) {
                    strEditarSenha.setVisibility(View.GONE);
                }
            }
        });
        edtNovaSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (edtNovaSenha.getText().toString().length() > 0) {
                    strEditarSenha.setVisibility(View.VISIBLE);
                } else if (edtSenhaAntiga.getText().toString().length() == 0) {
                    strEditarSenha.setVisibility(View.GONE);
                }
            }
        });
        strEditarSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    edtSenhaAntiga.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtNovaSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    edtSenhaAntiga.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtNovaSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        edtNomeProfessorEdite.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        edtMatriculaProfessorEdite.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        edtEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
    }
    public void backPerfil(){
        Intent intent = new Intent(getApplicationContext(), Perfil.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        startActivity(new Intent(EditePerfil.this, Perfil.class));
        EditePerfil.this.finish();
    }
    public void instance(){
        tbEditePerfil = (Toolbar)findViewById(R.id.tbEditePerfil);
        iptMatriculaProfessorEdite = (TextInputLayout)findViewById(R.id.iptMatriculaProfessorEdite);
        iptNomeProfessorEdite = (TextInputLayout)findViewById(R.id.iptNomeProfessorEdite);
        edtMatriculaProfessorEdite = (EditText)findViewById(R.id.edtMatriculaProfessorEdite);
        edtNomeProfessorEdite = (EditText)findViewById(R.id.edtNomeProfessorEdite);
        ivEditePerfil = (CircleImageView)findViewById(R.id.ivEditePerfil);
        btnConcluirEdite = (Button)findViewById(R.id.btnConcluirEdite);
        llEditePerfil = (LinearLayout)findViewById(R.id.llEditePerfil);
        edtNovaSenha = (EditText)findViewById(R.id.edtNovaSenha);
        edtSenhaAntiga = (EditText)findViewById(R.id.edtSenhaAntiga);
        iptNovaSenha = (TextInputLayout)findViewById(R.id.iptNovaSenha);
        iptSenhaAntiga = (TextInputLayout)findViewById(R.id.iptSenhaAntiga);
        btnEditarSenha = (Button)findViewById(R.id.btnEditarSenha);
        llEditarSenha = (LinearLayout)findViewById(R.id.llEditarSenha);
        strEditarSenha = (SwitchCompat)findViewById(R.id.strEditarSenha);
        iptEmail = (TextInputLayout)findViewById(R.id.iptEmail);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        setSupportActionBar(tbEditePerfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        try {
            if(resultCode == RESULT_OK && requestCode == IMAGEM_INTERNA){

                Uri imagemSelecionada = intent.getData();

                String[] colunas = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imagemSelecionada, colunas, null, null, null);
                cursor.moveToFirst();

                int indexColuna = cursor.getColumnIndex(colunas[0]);
                professorPerfil.setUriFoto(cursor.getString(indexColuna));
                cursor.close();

                ivEditePerfil.setImageBitmap(ImageUtil.setPic(Uri.parse(professorPerfil.getUriFoto()), ivEditePerfil.getWidth(), ivEditePerfil.getHeight()));
            }
        }catch (Exception e){
            Snackbar.make(llEditePerfil, "ocorreu um erro, tente novamente!", Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}


