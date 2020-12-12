package com.example.projetrecette.NavigationBar.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.example.projetrecette.Recette.AffichageRecette;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView image1, image2, image3, image4;
    StorageReference mStorageRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ArrayList<ImageView> arrayList = new ArrayList<ImageView>();



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setAttribut(view);
        getQuery();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAttribut(getView());
        getQuery();

    }

    public void getQuery(){

        fStore.collection("recipes").orderBy("Click").limit(4).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int i = 0;
                    for(final QueryDocumentSnapshot document : task.getResult()){
                        if(document.exists()){
                            StorageReference pathphoto1 = mStorageRef.child("Recipes_pics").child(document.getString("Recipe_Pic"));
                            /* Si problème rebuild le projet */
                            GlideApp.with(getActivity().getApplicationContext()).load(pathphoto1).into(arrayList.get(i));
                            arrayList.get(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent j = new Intent(getActivity().getApplicationContext(), AffichageRecette.class);
                                    j.putExtra("key", document.getId());
                                    startActivity(j);
                                }
                            });


                            i++;
                        }
                    }
                }
            }
        });
    }

    public void setAttribut(View v){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        this.image1 = v.findViewById(R.id.home_image_1);
        this.image2 = v.findViewById(R.id.home_image_2);
        this.image3 = v.findViewById(R.id.home_image_3);
        this.image4 = v.findViewById(R.id.home_image_4);
        arrayList.add(image1);
        arrayList.add(image2);
        arrayList.add(image3);
        arrayList.add(image4);


    }







}