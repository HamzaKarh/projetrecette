package com.example.projetrecette.Drawer.MonCompte;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends AppCompatActivity {
    Button buttonChangePassword;
    TextInputLayout newPassword;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    TextInputLayout Password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        addToolbar();
        setAttribut();
        changeCurrentPassword();
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

    public boolean validateNewPassword(){
        String newpassword = newPassword.getEditText().getText().toString().trim();
        if(newpassword.isEmpty()){
            newPassword.setError("Entrer un Mot de Passe");
            return false;
        } else if(newpassword.length() < 6){
            newPassword.setError("Le Mot de passe doit contenir au moins 6 caractères");
            return false;
        }else{
            newPassword.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        String password = Password.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            Password.setError("Entrer votre Mot de Passe");
            return false;
        }else{
            return true;
        }
    }
    public void changeCurrentPassword(){
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateNewPassword() && validatePassword()){
                    final String password = Password.getEditText().getText().toString().trim();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(" Re-authentication  ", " User re-authenticated.");
                                        updatePassword(newPassword.getEditText().getText().toString().trim());
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

    public void updatePassword(String newPass){
        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(" Password ", "Successfully updated");
                    Toast.makeText(getApplicationContext(),"Mot de passe Modifié", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else{
                    Log.d(" Password ", "Update Failed");
                    Toast.makeText(ChangePassword.this, "Erreur", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setAttribut(){
        buttonChangePassword = findViewById(R.id.button_change_password);
        newPassword = findViewById(R.id.text_input_new_password );
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        Password = findViewById(R.id.password_check_password);
    }
}
