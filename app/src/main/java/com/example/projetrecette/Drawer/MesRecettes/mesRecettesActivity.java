package com.example.projetrecette.Drawer.MesRecettes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.projetrecette.Drawer.Login.LoginActivity;
import com.example.projetrecette.R;
import com.example.projetrecette.Recette.newRecetteActivity;

public class mesRecettesActivity extends AppCompatActivity {

    ImageView btnAddRecette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_recettes);
        btnAddRecette = findViewById(R.id.add_recette);
        addToolbar();
        onClickAddRecette();
    }



    public void onClickAddRecette(){
        btnAddRecette.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), newRecetteActivity.class);
                startActivity(intent);
            }
        }));
    }

    public void addToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}