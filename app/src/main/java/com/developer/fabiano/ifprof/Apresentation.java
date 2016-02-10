package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.developer.fabiano.ifprof.adapters.AdapterMenuApresentation;

public class Apresentation extends AppCompatActivity {
    private Button btnAnterior,btnProximo,btnFechar;
    private ViewPager viewPager;
    private LinearLayout llButtom;
    private AdapterMenuApresentation adapterMenuApresentation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_apresentation);
        btnAnterior = (Button)findViewById(R.id.btnAnterior);
        btnProximo = (Button)findViewById(R.id.btnProximo);
        btnAnterior.setVisibility(View.GONE);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        btnFechar = (Button)findViewById(R.id.btnFechar);
        llButtom = (LinearLayout)findViewById(R.id.llButtom);
        llButtom.setBackgroundColor(getResources().getColor(R.color.tab1));
        adapterMenuApresentation = new AdapterMenuApresentation(getSupportFragmentManager(), 3);
        if (savedInstanceState != null){
            viewPager.setCurrentItem(savedInstanceState.getInt("position"));
        }
        viewPager.setAdapter(adapterMenuApresentation);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btnAnterior.setVisibility(View.GONE);
                    llButtom.setBackgroundColor(getResources().getColor(R.color.tab1));
                } else {
                    btnAnterior.setVisibility(View.VISIBLE);
                    if (position == 2) {
                        btnProximo.setText("Fechar");
                        llButtom.setBackgroundColor(getResources().getColor(R.color.tab3));
                    } else {
                        btnProximo.setText("Próximo");
                        llButtom.setBackgroundColor(getResources().getColor(R.color.tab2));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAnterior.setVisibility(View.VISIBLE);
                if (btnProximo.getText().toString().equals("Próximo")) {
                    int position = viewPager.getCurrentItem() + 1;
                    if (position != 3) {
                        viewPager.setCurrentItem(position);
                    } else {
                        btnProximo.setText("Fechar");
                    }
                } else {
                    startActivity(new Intent(Apresentation.this, MenuMain.class));
                    Apresentation.this.finish();
                }
            }
        });
        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem() - 1;
                if (position != -1) {
                    viewPager.setCurrentItem(position);
                } else {
                    btnAnterior.setVisibility(View.GONE);
                }
                btnProximo.setText("Próximo");
            }
        });
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Apresentation.this, MenuMain.class));
                Apresentation.this.finish();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position",viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }
}
