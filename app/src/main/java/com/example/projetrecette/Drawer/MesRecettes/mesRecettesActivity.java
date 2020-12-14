package com.example.projetrecette.Drawer.MesRecettes;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.example.projetrecette.Recette.AffichageRecette;
import com.example.projetrecette.Recette.Allergie;
import com.example.projetrecette.Recette.RecipeModel;
import com.example.projetrecette.Recette.newRecetteActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class mesRecettesActivity extends AppCompatActivity {

    ImageView btnAddRecette;
    StorageReference mStorageRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    RecyclerView mResultList;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_recettes);
        btnAddRecette = findViewById(R.id.add_recette);
        addToolbar();
        setAttribut();
        onClickAddRecette();
        getQuery();

    }

    public void getQuery(){
        Query query = fStore.collection("recipes").whereEqualTo("Auteur", userId);
        FirestoreRecyclerOptions<RecipeModel> options = new FirestoreRecyclerOptions.Builder<RecipeModel>().setQuery(query, RecipeModel.class).build();
        adapter = new FirestoreRecyclerAdapter<RecipeModel, RecipeModelViewHolder>(options) {

            @NonNull
            @Override
            public RecipeModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_recipe, parent, false);
                return new RecipeModelViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecipeModelViewHolder holder, final int position, @NonNull final RecipeModel model) {
                holder.setRecipe(model);
            }
        };

        mResultList.setLayoutManager(new LinearLayoutManager(this));
        mResultList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private class RecipeModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, author, cookingtime;
        TextView A,CR,CE,F,G,M,L,P;
        RatingBar rating;
        ImageView image;
        String recipeid;


        public RecipeModelViewHolder(@NonNull View itemView){
            super(itemView);
            this.name = itemView.findViewById(R.id.recipe_name);
            this.author = itemView.findViewById(R.id.recipe_author);
            this.rating = itemView.findViewById(R.id.recipe_rating);
            this.image = itemView.findViewById(R.id.recipe_image);
            iniAllergy();
            itemView.setOnClickListener(this);

        }

        public void iniAllergy(){
            this.A = itemView.findViewById(R.id.item_arachid);
            this.CR = itemView.findViewById(R.id.item_crustace);
            this.CE = itemView.findViewById(R.id.item_celeri);
            this.F = itemView.findViewById(R.id.item_fruit);
            this.G = itemView.findViewById(R.id.item_gluten);
            this.M = itemView.findViewById(R.id.item_moutarde);
            this.L = itemView.findViewById(R.id.item_lait);
            this.P = itemView.findViewById(R.id.item_poisson);
        }

        public void Allergy(Allergie allergie){
            if(allergie.arachid){this.A.setVisibility(View.VISIBLE);}
            if(allergie.crustace){this.CR.setVisibility(View.VISIBLE);}
            if(allergie.celeri){this.CE.setVisibility(View.VISIBLE);}
            if(allergie.fruitcoq){this.F.setVisibility(View.VISIBLE);}
            if(allergie.gluten){this.G.setVisibility(View.VISIBLE);}
            if(allergie.moutarde){this.M.setVisibility(View.VISIBLE);}
            if(allergie.lait){this.L.setVisibility(View.VISIBLE);}
            if(allergie.poisson){this.P.setVisibility(View.VISIBLE);}
        }



        public void setRecipe(RecipeModel recipe){
            this.name.setText(recipe.getNom_Recette());
            this.rating.setRating(Float.parseFloat(recipe.getRating()));
            this.recipeid = recipe.getRecipe_id();

            final TextView aut = this.author;
            final ImageView img = this.image;
            System.out.println(recipe.getAllergies());
            Allergy(recipe.getAllergies());

            /*Get Username*/
            fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(recipe.getAuteur()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String auteur = "Par " + documentSnapshot.getString("Fullname");
                    aut.setText(auteur);
                }
            });

            /*Get Photo*/
            if(recipe.getRecipe_id().equals("placeholder") == false){
                fStore.collection("recipes").document(recipe.getRecipe_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final StorageReference pathphoto1 = mStorageRef.child("Recipes_pics").child(documentSnapshot.getString("Recipe_Pic"));
                        /* Si problème rebuild le projet */
                        GlideApp.with(getApplicationContext()).load(pathphoto1).into(img);
                    }
                });
            }

        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(), AffichageRecette.class);
            i.putExtra("key", recipeid);
            startActivity(i);
        }
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

    public void setAttribut(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        mResultList = findViewById(R.id.recycler_view);
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