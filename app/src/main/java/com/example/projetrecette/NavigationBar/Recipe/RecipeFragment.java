package com.example.projetrecette.NavigationBar.Recipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.example.projetrecette.Recette.AffichageRecette;
import com.example.projetrecette.Recette.RecipeModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.view.inputmethod.InputMethodManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.F
 */
public class RecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private FirestoreRecyclerAdapter adapter;
    FirebaseFirestore fStore;

    StorageReference mStorageRef;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        mSearchField = view.findViewById(R.id.search_recipe);
        mSearchBtn = view.findViewById(R.id.search_button);

        setAttribut(view);

        fStore = FirebaseFirestore.getInstance();
        mResultList = view.findViewById(R.id.result_list);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebaseRecipeSearch(mSearchField.getText().toString());
                getQuery(mSearchField.getText().toString());
                adapter.startListening();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch(i){
                        case KeyEvent.KEYCODE_ENTER:
                            getQuery(mSearchField.getText().toString());
                            adapter.startListening();
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mResultList.setAdapter(adapter);


        return view;
    }


    private class RecipeModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, author, cookingtime;
        RatingBar rating;
        ImageView image;
        String recipeid;



        public RecipeModelViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.recipe_name);
            this.author = itemView.findViewById(R.id.recipe_author);
            this.rating = itemView.findViewById(R.id.recipe_rating);
            this.image = itemView.findViewById(R.id.recipe_image);
            itemView.setOnClickListener(this);
        }


        public void setRecipe(RecipeModel recipe){
            this.name.setText(recipe.getNom_Recette());
            this.rating.setRating(Float.parseFloat(recipe.getRating()));
            this.recipeid = recipe.getRecipe_id();

            final TextView aut = this.author;
            final ImageView img = this.image;

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


    public void setAttribut(View v ){
        fStore = FirebaseFirestore.getInstance();
        mResultList = v.findViewById(R.id.recycler_view);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }





    public void getQuery(String s){
        Query query = fStore.collection("recipes").whereEqualTo("Nom_Recette", s);
        FirestoreRecyclerOptions<RecipeModel> options = new FirestoreRecyclerOptions.Builder<RecipeModel>().setQuery(query, RecipeModel.class).build();
        adapter = new FirestoreRecyclerAdapter<RecipeModel, RecipeModelViewHolder>(options) {

            @NonNull
            @Override
            public RecipeModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_recipe, parent, false);
                return new RecipeModelViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecipeModelViewHolder holder, int position, @NonNull RecipeModel model) {
                holder.setRecipe(model);
            }
        };

        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mResultList.setAdapter(adapter);
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


}