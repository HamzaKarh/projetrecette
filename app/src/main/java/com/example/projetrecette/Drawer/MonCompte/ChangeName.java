package com.example.projetrecette.Drawer.MonCompte;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ViewUtils;

import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeName extends AppCompatActivity {

    Button buttonchangeName;
    TextInputLayout newName;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    TextInputLayout Password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        addToolbar();
        setAttribut();
        changeName();
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

    public void changeName(){
        buttonchangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName() && validatePassword()){
                    final String password = Password.getEditText().getText().toString().trim();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(" Re-authentication  ", " User re-authenticated.");
                                        updateName(newName.getEditText().getText().toString().trim());
                                    } else {
                                        Log.d("Re-authentication  ", "Failed");
                                        Password.setError("Mot de passe incorrect");
                                    }
                                }
                            });
                }
            }
            });
    }


    public boolean validateName(){
        String name = newName.getEditText().getText().toString().trim();
        if(name.isEmpty()){
            newName.setError("Veuillez donner un NOM et un PRENOM");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword(){
        String password = Password.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            Password.setError("Veuillez entre votre Mot de Passe");
            return false;
        } else {
            Password.setError(null);
            return true;
        }
    }

    private void updateName(String NewName){
        fStore.collection("users")
                .document(user.getUid()).update("Fullname",NewName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(" Update Name ", "Name Successfully Updated");
                        Toast.makeText(ChangeName.this, "Nom Modidifié", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }

    public void setAttribut(){
        buttonchangeName = findViewById(R.id.button_change_name);
        newName = findViewById(R.id.text_input_name);
        Password = findViewById(R.id.name_password);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
    }


}
