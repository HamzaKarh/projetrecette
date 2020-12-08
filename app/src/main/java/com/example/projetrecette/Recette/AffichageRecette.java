package com.example.projetrecette.Recette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AffichageRecette extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference mStorageRef;
    String userId;
    String recipeId;
    TextView nomRecette, author, prep, cuisson, ingredient, recette;
    RatingBar difficulty, rating;
    ImageView image, btn_favoris, btn_edit;
    boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_recette);
        addToolbar();
        setAttribut();
        recipeId = getIntent().getStringExtra("key");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialisation();
        favoris();
    }

    public void favoris(){

        this.btn_favoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavourite){
                    btn_favoris.setColorFilter(Color.parseColor("#9e9e9e"));
                    isFavourite = false;
                }else{
                    btn_favoris.setColorFilter(Color.parseColor("#f48fb1"));
                    isFavourite = true;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        fStore.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> o = (ArrayList<String>) documentSnapshot.getData().get("Mes_Favoris");
                if(o.contains(recipeId) && !isFavourite){
                    o.remove(recipeId);
                    fStore.collection("users").document(userId).update("Mes_Favoris", o);
                }
                if(!o.contains(recipeId) && isFavourite){
                    o.add(recipeId);
                    fStore.collection("users").document(userId).update("Mes_Favoris", o);
                }
            }
        });
    }

    public void initialisation(){

        this.nomRecette = findViewById(R.id.affichage_nom_recette);
        this.author = findViewById(R.id.affichage_author);
        this.prep = findViewById(R.id.affichage_prep);
        this.cuisson = findViewById(R.id.affichage_cuisson);
        this.ingredient = findViewById(R.id.affichage_ingre);
        this.recette = findViewById(R.id.affichage_recette);
        this.image = findViewById(R.id.affichage_image);
        this.rating = findViewById(R.id.affichage_ratingbar);
        this.difficulty = findViewById(R.id.affichage_difficulty);
        this.btn_favoris = findViewById(R.id.affichage_favoris);
        this.btn_edit = findViewById(R.id.affichage_edit);
        btn_favoris.setColorFilter(Color.parseColor("#9e9e9e"));

        fStore.collection("recipes").document(recipeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                fStore.collection("users").document(documentSnapshot.getString("Auteur")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String auteur = "Par " + documentSnapshot.getString("Fullname");
                        author.setText(auteur);
                    }
                });
                String b = documentSnapshot.getString("Temps_Preparation") + " min";
                String c = documentSnapshot.getString("Temps_Cuisson") + " min";
                nomRecette.setText(documentSnapshot.getString("Nom_Recette"));
                rating.setRating(Float.parseFloat(documentSnapshot.getString("Rating")));
                difficulty.setRating(Float.parseFloat(documentSnapshot.getString("Difficulty")));
                prep.setText(b);
                cuisson.setText(c);
                ingredient.setText(Html.fromHtml(documentSnapshot.getString("Ingredient")));
                recette.setText(Html.fromHtml(documentSnapshot.getString("Recette")));
                final StorageReference pathphoto = mStorageRef.child("Recipes_pics").child(documentSnapshot.getString("Recipe_Pic"));
                GlideApp.with(getApplicationContext()).load(pathphoto).into(image);

                if(userId.equals(documentSnapshot.getString("Auteur"))){
                    btn_edit.setVisibility(View.VISIBLE);
                    onClickEdit();
                }else{
                    btn_edit.setVisibility(View.INVISIBLE);
                }

            }
        });
        fStore.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> o = (ArrayList<String>) documentSnapshot.getData().get("Mes_Favoris");
                if(o.contains(recipeId)){
                    btn_favoris.setColorFilter(Color.parseColor("#f48fb1"));
                    isFavourite = true;
                }
            }
        });


    }

    public void onClickEdit(){
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), editRecetteActivity.class);
                i.putExtra("key", recipeId);
                startActivity(i);
            }
        });
    }



    public void setAttribut(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();


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