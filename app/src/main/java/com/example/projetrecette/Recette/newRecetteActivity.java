package com.example.projetrecette.Recette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.projetrecette.GlideApp;
import com.example.projetrecette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class newRecetteActivity extends AppCompatActivity {

    EditText name_recette, temps_prep, temps_cook, multi_ingre, multi_recette;
    RatingBar difficulty;
    ImageView image;
    Button btncreateRecipe;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, recipeId, uploadId;
    StorageReference storageReference;
    StorageTask mUploadTask;
    DocumentReference drefRecipe;
    Uri ImageUri;
    CollectionReference crefRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recette);
        addToolbar();
        setAttribut();
        onclickCreateRecipe();
        selectImage();

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (ImageUri != null) {
            /* On utilise UUID pour obtenir une id random */
            final StorageReference fileReference = storageReference.child("Recipes_pics").child(UUID.randomUUID().toString()
            + "." + getFileExtension(ImageUri));

            mUploadTask = fileReference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Sucess", Toast.LENGTH_SHORT).show();
                    drefRecipe.update("Recipe_pic", fileReference.toString());
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
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            /*
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, 1);
                            */
                            dialog.dismiss();
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 2);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            ImageUri = data.getData();
            GlideApp.with(getApplicationContext()).load(ImageUri).into(image);
        }
    }


    private void onclickCreateRecipe(){
        btncreateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_recette.getText().toString().trim();
                String temps_prepation = temps_prep.getText().toString().trim();
                String temps_cooking = temps_cook.getText().toString().trim();
                String multi_ingredient = multi_ingre.getText().toString().trim();
                String multi_rec = multi_recette.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    name_recette.setError("Veuillez mettre un nom de recette valide!");
                    return;
                }
                if(TextUtils.isEmpty(temps_cooking)){
                    temps_cooking = "0";
                }
                if (TextUtils.isEmpty(temps_prepation)){
                    temps_prepation = "0";
                }
                if(TextUtils.isEmpty(multi_ingredient)){
                    multi_ingredient = "L'auteur n'a pas donner d'ingrédient";
                }
                if(TextUtils.isEmpty(multi_rec)){
                    multi_rec = "L'auteur n'a pas donné d'étapes à suivre pour la réalisation de la recette";
                }

                final DocumentReference drefUser = fStore.collection("users").document(userId);
                crefRecipe = fStore.collection("recipes");
                Map<String,Object> recipe = new HashMap<>();
                recipe.put("Auteur", userId);
                recipe.put("Nom_Recette", name);
                recipe.put("Temps_Preparation", temps_prepation);
                recipe.put("Temps_Cuisson", temps_cooking);
                recipe.put("Ingredient", multi_ingredient);
                recipe.put("Recette", multi_rec);
                recipe.put("Recipe_Pic", "default_pic.png");
                crefRecipe.add(recipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        recipeId = documentReference.getId();
                        drefRecipe = fStore.collection("recipes").document(recipeId);
                        drefUser.update("Mes_Recettes", FieldValue.arrayUnion(recipeId));
                        uploadFile();
                        Toast.makeText(getApplicationContext(), "Sucess Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
                onBackPressed();
            }
        });
    }


    private void setAttribut(){
        name_recette = findViewById(R.id.creation_name);
        temps_prep = findViewById(R.id.creation_preparation);
        temps_cook = findViewById(R.id.creation_cooking);
        multi_ingre = findViewById(R.id.creation_ingredient);
        multi_recette = findViewById(R.id.creation_recette);
        difficulty = findViewById(R.id.creation_difficulty);
        btncreateRecipe = findViewById(R.id.btnCreationRecette);
        image = findViewById(R.id.creation_image);
        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
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