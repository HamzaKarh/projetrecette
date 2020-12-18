package com.example.projetrecette.NavigationBar.Favorite;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.projetrecette.Drawer.MesRecettes.mesRecettesActivity;
import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.example.projetrecette.Recette.AffichageRecette;
import com.example.projetrecette.Recette.Allergie;
import com.example.projetrecette.Recette.RecipeModel;
import com.example.projetrecette.Recette.newRecetteActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.callback.Callback;


public class FavoriteFragment extends Fragment {

    StorageReference mStorageRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    RecyclerView mResultList;
    FirestoreRecyclerAdapter adapter;
    ArrayList<String> listFav;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){

            View view = inflater.inflate(R.layout.fragment_favorite_logged, container, false);
            setAttribut();
            listFav = new ArrayList<String>();
            mResultList = view.findViewById(R.id.recycler_view_fav);


            return view;
        }else{
            return inflater.inflate(R.layout.fragment_favorite, container, false);
        }

    }


    public void getFav(){
        fStore.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                listFav = (ArrayList<String>) documentSnapshot.getData().get("Mes_Favoris");
                System.out.println(listFav);

            }
        });
    }

    public void getQuery2(){

        Query query = fStore.collection("recipes").whereIn("Recipe_id", listFav);
        //Query query = fStore.collection("recipes").whereEqualTo("Auteur", userId);
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

        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mResultList.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onResume() {
        super.onResume();
        if(userId != null){
            getFav();

            CountDownTimer c = new CountDownTimer(1000,500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if(!listFav.isEmpty()){
                        getQuery2();
                    }
                }
            };
            c.start();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    private class RecipeModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, author;
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
                        GlideApp.with(getActivity().getApplicationContext()).load(pathphoto1).into(img);
                    }
                });
            }
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity().getApplicationContext(), AffichageRecette.class);
            i.putExtra("key", recipeid);
            startActivity(i);
        }
    }





    public void setAttribut(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();

    }


}