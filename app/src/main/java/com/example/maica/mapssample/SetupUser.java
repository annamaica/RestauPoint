package com.example.maica.mapssample;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SetupUser extends AppCompatActivity {

    EditText editTextUsername, editTextLastname, editTextFirstname;
    Button buttonSetupUser;
    FirebaseAuth firebaseAuth;
    StorageReference mStorageRef;
    DatabaseReference databaseUser;
    ImageView AddImage;
    Uri imgUri;

    public static final String STORAGE_PATH = "userimage/";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user);
        setTitle("Setup Information");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        editTextUsername= (EditText) findViewById(R.id.editTextUsername);
        editTextLastname = (EditText) findViewById(R.id.editTextLastname);
        editTextFirstname = (EditText) findViewById(R.id. editTextFirstname);
        buttonSetupUser = (Button) findViewById(R.id.buttonSetupUser);
        AddImage = (ImageView) findViewById(R.id.AddImage);

        buttonSetupUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
    }

    public void btnBrowseImage(View view){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                AddImage.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addUser(){
        if (imgUri != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading image");
            dialog.show();

            //Get the storage reference
            StorageReference ref = mStorageRef.child(STORAGE_PATH + System.currentTimeMillis() +"."+getImageExt(imgUri));

            //Add file to reference

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //dismiss dialog when success
                    dialog.dismiss();

                    //Save image info in to database

                    String username = editTextUsername.getText().toString().trim();
                    String lastname = editTextLastname.getText().toString().trim();
                    String firstname = editTextFirstname.getText().toString().trim();


                    if (TextUtils.isEmpty(username)) {
                        Toast.makeText(getApplicationContext(), "Please fill up the details", Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(lastname)) {
                        Toast.makeText(getApplicationContext(), "Please fill up the details", Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(firstname)) {
                        Toast.makeText(getApplicationContext(), "Please fill up the details", Toast.LENGTH_LONG).show();
                    }
                    else{
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String usertypess = "normal";
                        ProfileInformation profile = new ProfileInformation(user.getUid(),username, lastname, firstname, firstname, taskSnapshot.getDownloadUrl().toString(), usertypess);
                        databaseUser.child(user.getUid()).setValue(profile);

                        //display success toast
                        finish();
                        Toast.makeText(getApplicationContext(), "Account Complete!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TabActivity.class));
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //dismiss dialog when fail
                            dialog.dismiss();
                            //display success toast
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //show upload progress

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploading " + (int)progress+"");
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please select image to upload", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        finish();
    }
}
