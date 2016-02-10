package com.developer.fabiano.ifprof;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Professor;


public class SetPhoto extends AppCompatActivity {
    private Button btnSetPhoto;
    private LinearLayout llSetPhoto;
    private ImageView ivSetPhoto;
    private Toolbar tbSetPhoto;
    private Repositorio repositorio;
    public static final int IMAGEM_INTERNA = 1;
    private int tipo = 0;
    private Professor professor;
    private Button btnNewFoto;
    private Button btnExistente;
    boolean showed = false;
    String pathImg;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_photo);
        tbSetPhoto = (Toolbar) findViewById(R.id.tbSetPhoto);
        setSupportActionBar(tbSetPhoto);
        this.savedInstanceState = savedInstanceState;
        llSetPhoto = (LinearLayout) findViewById(R.id.llSetPhoto);
        ivSetPhoto = (ImageView) findViewById(R.id.ivSetPhoto);
        btnSetPhoto = (Button) findViewById(R.id.btnSetPhoto);
        btnNewFoto = (Button)findViewById(R.id.btnNewFoto);
        btnExistente = (Button)findViewById(R.id.btnExistente);
        btnExistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentGalery();
            }
        });
        try {
            btnNewFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipo = 1;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, IMAGEM_INTERNA);
                }
            });
            ivSetPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentGalery();
                }
            });
        }catch (Exception e){}
        try {
            repositorio = new Repositorio(SetPhoto.this);
            professor = repositorio.getLogged();
            repositorio.close();
        }catch (Exception e){
            AlertDialog.Builder x = new AlertDialog.Builder(SetPhoto.this);
            x.setMessage(e.getMessage()).setNeutralButton("ok",null).show();
        }
        btnSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    repositorio = new Repositorio(SetPhoto.this);
                    repositorio.update(DataBase.TABLE_PROFESSOR, DataBase.URIFOTO, "'" + pathImg + "'", DataBase.ID_PROFESSOR, professor.getId(), "");
                    repositorio.close();
                }catch (Exception e){
                    AlertDialog.Builder x = new AlertDialog.Builder(SetPhoto.this);
                    x.setMessage(e.getMessage()).setNeutralButton("ok",null).show();
                    e.printStackTrace();
                }
                Intent intent = new Intent(SetPhoto.this, MenuMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this will clear all the stack
                intent.putExtra("Exit me", true);
                startActivity(intent);
                startActivity(new Intent(SetPhoto.this, MenuMain.class));
                SetPhoto.this.finish();
            }
        });
        if (savedInstanceState != null){
            btnSetPhoto.setText(savedInstanceState.getString("button"));
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            if (!showed){
                if (savedInstanceState != null){
                    pathImg = savedInstanceState.getString("image");
                    ivSetPhoto.setImageBitmap(ImageUtil.setPic(Uri.parse(pathImg), ivSetPhoto.getWidth(), ivSetPhoto.getHeight()));
                }else if (getIntent().getStringExtra("image") != null && !getIntent().getStringExtra("image").equals("null")) {
                    pathImg = getIntent().getStringExtra("image");
                    Bitmap bitmap = ImageUtil.setPic(Uri.parse(getIntent().getStringExtra("image")), ivSetPhoto.getWidth(), ivSetPhoto.getHeight());
                    if(bitmap != null){
                        ivSetPhoto.setImageBitmap(bitmap);
                    }
                    btnSetPhoto.setText("Feito");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                showed = true;
            }
        }catch (Exception e){}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("image",pathImg);
        outState.putString("button",btnSetPhoto.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void intentGalery(){
        tipo = 0;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Selecione uma imagem"), IMAGEM_INTERNA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        try {
            if (resultCode == RESULT_OK){
                if(requestCode == IMAGEM_INTERNA && tipo == 0){
                    Uri imagemSelecionada = intent.getData();
                    pathImg = AlertsAndControl.getPath(imagemSelecionada,SetPhoto.this);
                    Bitmap bitmap = ImageUtil.setPic(Uri.parse(pathImg), ivSetPhoto.getWidth(), ivSetPhoto.getHeight());
                    if (bitmap != null){
                        ivSetPhoto.setImageBitmap(bitmap);
                    }else{
                        Snackbar.make(llSetPhoto, "Ocorreu um erro, escolha outro app para abrir a imagem ou tente novamente!", Snackbar.LENGTH_LONG).show();
                        Log.i("image galeria","image null");
                    }
                    btnSetPhoto.setText("Feito");
                }else if(requestCode == IMAGEM_INTERNA){
                    //imagem veio da camera
                    Bundle extras = intent.getExtras();
                    Bitmap imagem = (Bitmap) extras.get("data");
                    pathImg = AlertsAndControl.savePhoto(imagem,this,professor,false);
                    Bitmap bitmap = ImageUtil.setPic(Uri.parse(pathImg), ivSetPhoto.getWidth(), ivSetPhoto.getHeight());
                    if (bitmap != null){
                        ivSetPhoto.setImageBitmap(bitmap);
                    }else{
                        Snackbar.make(llSetPhoto, "Ocorreu um erro, escolha outro app para abrir a imagem ou tente novamente!", Snackbar.LENGTH_LONG).show();
                        Log.i("image camera","image null");
                    }
                    btnSetPhoto.setText("Feito");
                }
            }
        }catch (Exception e){
            Snackbar.make(llSetPhoto, "Ocorreu um erro, escolha outro app para abrir a imagem ou tente novamente!", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
