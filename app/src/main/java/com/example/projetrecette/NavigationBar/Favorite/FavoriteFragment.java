package com.example.projetrecette.NavigationBar.Favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projetrecette.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FavoriteFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            return inflater.inflate(R.layout.fragment_favorite_logged, container, false);
        }else{
            return inflater.inflate(R.layout.fragment_favorite, container, false);
        }

    }
}