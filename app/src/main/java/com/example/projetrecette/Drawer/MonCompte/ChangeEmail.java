package com.example.projetrecette.Drawer.MonCompte;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeEmail extends AppCompatActivity {

    Button buttonchangeMail;
    TextInputLayout newMail;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    TextInputLayout Password;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        addToolbar();
        setAttribut();
        changeMail();

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



    private boolean  validateEmail(){
        String email = newMail.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            newMail.setError("Email vide non valide");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newMail.setError("Entrez une address email valide");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword(){
        String password = Password.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            Password.setError("Veuillez entrer votre Mot de Passe");
            return false;
        } else {
            Password.setError(null);
            return true;
        }
    }



    public void changeMail() {
            buttonchangeMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v){
                        if (validateEmail() && validatePassword()){
                            final String password = Password.getEditText().getText().toString().trim();
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(" Re-authentication  ", " User re-authenticated.");
                                                uploadMail(newMail.getEditText().getText().toString().trim());
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

    public void uploadMail(final String newEmail){
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(" Upload Email ", " User email address uploaded");
                    updateMail(newEmail);
                    onBackPressed();
                }
            }
        });
    }
    private void updateMail(String NewMail){
        fStore.collection("users").document(user.getUid()).update("Email", NewMail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update Email ","User email address updated");
                        Toast.makeText(getApplicationContext(), "Email Modifié", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setAttribut(){
        buttonchangeMail = findViewById(R.id.button_change_email);
        newMail = findViewById(R.id.text_input_email);
        Password = findViewById(R.id.text_input_password);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
    }



}
