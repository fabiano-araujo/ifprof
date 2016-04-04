package com.developer.fabiano.ifprof;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterMenu;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;


public class MenuMain extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar tbMainMenu;
    private TabLayout tabLayout;
    private ViewPager vpMainMenu;
    private AdapterMenu myPagerAdapterMenu;
    private SearchView searchView;
    private Repositorio repositorio;
    String stringQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        instance();
        setSupportActionBar(tbMainMenu);
        boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0, new Intent("ALARME_DISPARADO"), PendingIntent.FLAG_NO_CREATE) == null);

        if(alarmeAtivo){
            Intent itAlarm = new Intent("ALARME_DISPARADO");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,itAlarm,0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),2200000, pendingIntent);
        }

        tabLayout.addTab(tabLayout.newTab().setText("PROFESSOR"));
        tabLayout.addTab(tabLayout.newTab().setText("ALUNO"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        myPagerAdapterMenu = new AdapterMenu(getSupportFragmentManager(),tabLayout.getTabCount(),1,null,null);
        vpMainMenu.setAdapter(myPagerAdapterMenu);

        vpMainMenu.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpMainMenu.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
        }
        if (savedInstanceState != null){
            stringQrcode = savedInstanceState.getString("qrcode");
            AlertsAndControl.tipoQrcode(this, stringQrcode);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_main, menu);
        return true;
    }
    public void instance(){
        tbMainMenu = (Toolbar)findViewById(R.id.tbMainMenu);
        vpMainMenu = (ViewPager)findViewById(R.id.pager);
        searchView = (SearchView)findViewById(R.id.action_search);
        tabLayout = (TabLayout) findViewById(R.id.tlMainMenu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Posicione a camera sobre o QR code");
        switch (id){
            case R.id.sairDaConta:
                try {
                    repositorio = new Repositorio(this);

                    repositorio.exit(repositorio.getLogged().getId());
                    repositorio.close();
                    startActivity(new Intent(this,Login.class));
                    this.finish();
                }catch (Exception e){
                    AlertDialog.Builder x = new AlertDialog.Builder(this);
                    x.setMessage(e.getMessage()).setNeutralButton("ok", null).show();
                }
                break;
            case R.id.itemLerQRCode:
                integrator.initiateScan();
                break;
            case R.id.itmLerQrCode:
                integrator.initiateScan();
                break;
            case R.id.action_search:
                startActivity(new Intent(this,SearchableActivity.class));
                break;
            case R.id.sobre:
                AlertDialog.Builder sobre = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.sobre,null);
                TextView textView = (TextView) view.findViewById(R.id.txtF);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://github.com/fabiano-araujo"));
                        startActivity(i);
                    }
                });
                sobre.setView(view).setNeutralButton("Ok",null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                stringQrcode = scanResult.getContents();
                AlertsAndControl.tipoQrcode(this, stringQrcode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setTitle("Sair?");
        x.setMessage("tem certeza que quer sair?").setPositiveButton("sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            finish();

            }
        }).setNegativeButton("Não",null).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("qrcode", stringQrcode);
        super.onSaveInstanceState(outState);
    }
}


