package com.example.projetrecette.NavigationBar.Recipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetrecette.MainActivity;
import com.example.projetrecette.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

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
    FirebaseFirestore mRecipeDatabase;


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

    private void firebaseRecipeSearch(String s) {

         CollectionReference ref = mRecipeDatabase.collection("recipes");
         Query query = ref.whereEqualTo("Nom_Recette", s);

        FirestoreRecyclerOptions<Recipe> options  = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class).build();


        adapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options) {

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
                return new RecipeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
                holder.mAuthor.setText(model.getName());
                holder.mName.setText(model.getName());
                holder.mRating.setText(model.getRating());
                holder.mTime.setText(model.getTime());
            }
        };

        adapter.startListening();

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mResultList.setAdapter(adapter);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
                mSearchField = view.findViewById(R.id.search_recipe);
        mSearchBtn = view.findViewById(R.id.search_button);

        mRecipeDatabase = FirebaseFirestore.getInstance();
        mResultList = view.findViewById(R.id.result_list);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseRecipeSearch(mSearchField.getText().toString());

            }
        });

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mResultList.setAdapter(adapter);


        return view;
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout root;
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.list_layout);
            mAuthor = itemView.findViewById(R.id.recipe_author);
            mName = itemView.findViewById(R.id.recipe_name);
            mTime = itemView.findViewById(R.id.recipe_time);
            mRating = itemView.findViewById(R.id.recipe_rating);


        }



        public TextView mAuthor, mName, mTime, mRating, mCookingTime, mRecipe, mIngredients;



    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onStart() {
        super.onStart();
    }


}