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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Edit_Profile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText editTextEditUsername, editTextEditLastName, editTextEditFirstName;
    private ImageView edituserimage;
    private Button buttonSaveInfo;
    private StorageReference mStorageRef;
    private Uri imgUri;

    public static final String STORAGE_PATH = "userimage/";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile);
        setTitle("Edit Profile");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        editTextEditUsername = (EditText) findViewById(R.id.editTextEditUsername);
        editTextEditLastName = (EditText) findViewById(R.id.editTextEditLastName);
        editTextEditFirstName = (EditText) findViewById(R.id.editTextEditFirstName);
        buttonSaveInfo = (Button) findViewById(R.id.buttonSaveInfo);
        edituserimage = (ImageView) findViewById(R.id.viewEditImageUser);

        buttonSaveInfo.setOnClickListener(this);

        String id = firebaseAuth.getCurrentUser().getUid();
        Query search = databaseReference.child(id);
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileInformation info = dataSnapshot.getValue(ProfileInformation.class);
                String userName = info.getUsername();
                String lastname = info.getLastname();
                String firstname = info.getFirstname();
                editTextEditUsername.setText(userName);
                editTextEditFirstName.setText(firstname);
                editTextEditLastName.setText(lastname);
                Glide.with(Edit_Profile.this).load(info.getUrl()).into(edituserimage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                edituserimage.setImageBitmap(bm);
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

    private void saveUserInformation(){
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

                    String username = editTextEditUsername.getText().toString().trim();
                    String firstname = editTextEditFirstName.getText().toString().trim();
                    String lastname = editTextEditLastName.getText().toString().trim();

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

                        databaseReference.child(user.getUid()).setValue(profile);

                        //display success toast
                        finish();
                        Toast.makeText(getApplicationContext(), "Account Updated!", Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        if (view == buttonSaveInfo){
            saveUserInformation();
        }
    }
}
