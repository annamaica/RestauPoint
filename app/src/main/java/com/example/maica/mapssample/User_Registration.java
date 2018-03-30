package com.example.maica.mapssample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class User_Registration extends AppCompatActivity implements View.OnClickListener{


    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__registration);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            //profile activity is here
            finish();
            startActivity(new Intent(getApplicationContext(), TabActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmpassword = editTextConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function executing further
            return;
        }
        if (TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function executing further
            return;
        }
        if (password.length() <= 5){
            //password is empty
            Toast.makeText(this,"You must have more than 5 characters in your password", Toast.LENGTH_SHORT).show();
            //stopping the function executing further
            return;
        }
        if (password.equals(confirmpassword)){
            //if valid email and password

            progressDialog.setMessage("Registering User");
            progressDialog.show();

            //creating new user
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //task is successfully registered

                                //finish();
                                startActivity(new Intent(getApplicationContext(), SetupUser.class));
                                Toast.makeText(User_Registration.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            }
                            if(!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(User_Registration.this, "Could not register. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(User_Registration.this, "Password does not match!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view){
        if (view == buttonRegister){
            registerUser();
        }
        if (view == textViewSignin){
            finish();
            startActivity(new Intent(this, Log_in.class));
        }
    }
}
