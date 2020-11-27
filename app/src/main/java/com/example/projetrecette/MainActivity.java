package com.example.projetrecette;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projetrecette.Drawer.Login.LoginActivity;
import com.example.projetrecette.Drawer.MesRecettes.mesRecettesActivity;
import com.example.projetrecette.Drawer.MonCompte.MonCompte;
import com.example.projetrecette.Drawer.SignUp.SignUpActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NavController navController;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.MainFragment);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        mStorageRef = FirebaseStorage.getInstance().getReference();


        Appbar();
        Drawer();
        checkLogin();


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void Appbar(){
        BottomNavigationView btnavView = findViewById(R.id.nav_view);

        NavigationUI.setupWithNavController(btnavView, navController);
    }

    public void Drawer(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.navigation_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_signup:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_profil:
                intent = new Intent(this, MonCompte.class);
                startActivity(intent);
                break;
            case R.id.navigation_vos_recettes:
                intent = new Intent(this, mesRecettesActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_signout:
                FirebaseAuth.getInstance().signOut();
                checkLogin();
                break;
        }
        return true;
    }

    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /*Items*/
            navigationView.getMenu().setGroupVisible(R.id.drawerLoggedIn,true);
            navigationView.getMenu().setGroupVisible(R.id.drawerUnlogged,false);

            /*Header*/
            navigationView.removeHeaderView(navigationView.getHeaderView(0));
            navigationView.inflateHeaderView(R.layout.drawer_header);

            /*Initialisation*/

            retrieveData();

        }else{
            /*Items*/
            navigationView.getMenu().setGroupVisible(R.id.drawerLoggedIn,false);
            navigationView.getMenu().setGroupVisible(R.id.drawerUnlogged,true);

            /*Header*/
            navigationView.removeHeaderView(navigationView.getHeaderView(0));
            navigationView.inflateHeaderView(R.layout.drawer_header_unlogged);
        }
    }

    public void retrieveData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_email);
        final TextView fullname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_fullname);
        final ImageView photoProfil = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.photoProfile);
        db.collection("users").document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                email.setText(documentSnapshot.getString("Email"));
                fullname.setText(documentSnapshot.getString("Fullname"));
                final StorageReference pathphoto = mStorageRef.child("Photo_de_Profil").child(documentSnapshot.getString("Photo_de_Profile"));
                /* Si problème rebuild le projet */
                GlideApp.with(getApplicationContext()).load(pathphoto).into(photoProfil);
            }
        });
    }

}


