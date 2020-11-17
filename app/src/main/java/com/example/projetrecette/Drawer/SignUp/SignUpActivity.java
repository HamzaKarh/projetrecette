package com.example.projetrecette.Drawer.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    EditText uFullname, uEmail, uPassword;
    Button RegisterBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        addToolbar();
        setAttribut();

        RegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    uEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    uPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    uPassword.setError("Password must be > 6 characters");
                }
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Sucess", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    public void setAttribut(){
        uFullname = findViewById(R.id.nameSignUp);
        uEmail = findViewById(R.id.emailSignUp);
        uPassword = findViewById(R.id.passwordSignUp);
        RegisterBtn = findViewById(R.id.btnSignUp);
        fAuth = FirebaseAuth.getInstance();
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
}