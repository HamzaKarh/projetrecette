package com.example.projetrecette.Drawer.MonCompte;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeEmail extends AppCompatActivity {

    private String password;
    Button buttonchangeMail;
    TextInputLayout newMail;
    TextInputEditText changeMail;

    private static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" + "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        addToolbar();
        buttonchangeMail = findViewById(R.id.button_change_email);
        newMail = findViewById(R.id.text_input_email);
        changeMail = findViewById(R.id.new_email);
        checkIdentity();

    }

    public void addToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Changement d'adresse mail");
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
            newMail.setError(null);
            return true;
        }
    }


    public void checkIdentity() {
        buttonchangeMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmail.this);

                    final View customAlert = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);
                        builder.setTitle("Mot de passe");
                    /*
                    final EditText input = new EditText(ChangeEmail.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setPadding(5,0,5,5);
                    input.setHint("Entrez votre mot de passe");
                    input.setBackgroundResource(R.drawable.ic_baseline_lock_24);
                    */

                    builder.setView(customAlert);

                    // Set up the buttons
                    builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // password =
                            /**
                            il faut coder la validation du mot de passe  pour le changement de mail
                             et pour le changement de mot de passe
                             Coder le changement de  nom, prénom
                             */
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }
        });

    }






}
