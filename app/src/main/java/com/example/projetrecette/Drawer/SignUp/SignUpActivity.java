package com.example.projetrecette.Drawer.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText uFullname, uEmail, uPassword;
    Button RegisterBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        addToolbar();
        setAttribut();

        RegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                final String fullname = uFullname.getText().toString().trim();

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
                            adddatatoFireBase(email,fullname);
                            onBackPressed();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void adddatatoFireBase(String email, String fullname){
        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);
        Map<String,Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("Fullname", fullname);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Sucess Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAttribut(){
        uFullname = findViewById(R.id.nameSignUp);
        uEmail = findViewById(R.id.emailSignUp);
        uPassword = findViewById(R.id.passwordSignUp);
        RegisterBtn = findViewById(R.id.btnSignUp);
        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
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