package com.example.projetrecette.Drawer.MonCompte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projetrecette.GlideApp;
import com.example.projetrecette.MainActivity;
import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.sql.SQLOutput;
import java.util.UUID;

public class MonCompte extends AppCompatActivity {

    NavigationView navigationView;
    private static final int GALLERY_REQUEST_CODE = 123;
    ImageView ProfilPicture;
    //Button edit;
    TextView fullname, email, changeEmail, changefullname;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, pictureDeleted;
    StorageReference storageReference;
    StorageTask mUploadTask;
    DocumentReference drefProfil;
    Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_compte);
        addToolbar();
        setAttribut();
        setProfilPicture();
        selectImage();
        onEmailClicked();



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


    public void onEmailClicked(){
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonCompte.this,ChangeEmail.class);
                startActivity(intent);
            }
        });
    }


    public void setProfilPicture(){
        drefProfil.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final StorageReference pathphoto = storageReference.child("Photo_de_Profil").child(documentSnapshot.getString("Photo_de_Profile"));
                GlideApp.with(getApplicationContext()).load(pathphoto).into(ProfilPicture);
                //GlideApp.with(getApplicationContext()).load(pathphoto).into(email);
                email.setText(documentSnapshot.getString("Email"));
                fullname.setText(documentSnapshot.getString("Fullname"));

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();
            uploadFile();
            ProfilPicture.setImageURI(ImageUri);
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void deleteFormerPicture(){
        drefProfil.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final StorageReference pathphoto = storageReference.child("Photo_de_Profil").child(pictureDeleted);
                final StorageReference storageReferenceUrl = FirebaseStorage.getInstance().getReferenceFromUrl(pathphoto.toString());
                System.err.println("url = "+ storageReferenceUrl);
                if(!pictureDeleted.equals("default_pic.png")) {
                    storageReferenceUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(pictureDeleted, "onSuccess: deleted file");
                            System.err.println("Successfully deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            //Log.d(pictureDeleted, "onFailure: did not delete file");
                            System.err.println("Error: did not delete file");
                        }
                    });}
            }
        });



    }

    private void uploadFile() {
        if (ImageUri != null) {
            /* On utilise UUID pour obtenir une id random */
            final StorageReference fileReference = storageReference.child("Photo_de_Profil").child(UUID.randomUUID().toString()
                    + "." + getFileExtension(ImageUri));

            mUploadTask = fileReference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    drefProfil.update("Photo_de_Profile", fileReference.getName());
                    deleteFormerPicture();
                    Toast.makeText(getApplicationContext(),"Changement réussis", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Erreur", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void selectImage() {
        final CharSequence[] options = { "Prendre une Photo", "Choisir une photo existante","Retirer la photo de profil","Annuler" };
        ProfilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Modifier");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Prendre une Photo")) {
                            /*
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, 1);
                            */
                            dialog.dismiss();
                        } else if (options[item].equals("Choisir une photo existante")) {
                            Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, GALLERY_REQUEST_CODE);
                        } else if(options[item].equals("Retirer la photo de profil")){
                            drefProfil.update("Photo_de_Profile", "default_pic.png");
                            final StorageReference pathphoto = storageReference.child("Photo_de_Profil").child("default_pic.png");
                            GlideApp.with(getApplicationContext()).load(pathphoto).into(ProfilPicture);
                            deleteFormerPicture();
                            Toast.makeText(getApplicationContext(),"changement réussis", Toast.LENGTH_SHORT).show();
                        } else if (options[item].equals("Annuler")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    public void getFormerPicture(){
        drefProfil.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                pictureDeleted = documentSnapshot.getString("Photo_de_Profile");
            }
        });
    }

    private void setAttribut(){
        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        drefProfil = fStore.collection("users").document(userId);
        getFormerPicture();
        storageReference = FirebaseStorage.getInstance().getReference();
        ProfilPicture = findViewById(R.id.photoProfile);
        navigationView = findViewById(R.id.navigationView);
        fullname = findViewById(R.id.account_fullname);
        email = findViewById(R.id.account_email);
        changeEmail = findViewById(R.id.change_email);
        changefullname = findViewById(R.id.change_username);
        //edit = findViewById(R.id.edit_button);
        //Utiliser ces méthodes pour changer el mot de pass, ou le mail,
        //fAuth.getCurrentUser(userId).updatePassword()
    }



}