package com.to426project.happyhourapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySignUp extends Activity implements View.OnClickListener {
    private static final String TAG = "ActivitySignUp";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;



    private EditText editTextName, editTextEmail,editTextPassword, editTextConfirmPassword, editTextAge;
    private Button buttonCancel, buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //user signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                } else{
                    //user signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
            }
        };
        buttonCancel.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createAccount(String email, final String password, String name, final Integer age){
        final String mEmail = email;
        final String mName= name;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(ActivitySignUp.this, "Register Success", Toast.LENGTH_SHORT).show();
                            //write to database
                            //
                            //
                            //
                            //
                            String id = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "new user id is ::: "+ id);
                            writeNewUser(id, mEmail, password, mName, age);


                        }else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ActivitySignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void writeNewUser(String uid, String email, String password, String name, Integer age) {
        User newUser = new User(email, password, uid, name, "", "", "", "", age, 0);
        final Intent logInSuccessIntent = new Intent(this , MainActivity.class);

        mDatabase.child("user").child(uid).setValue(newUser)
                .addOnCompleteListener(ActivitySignUp.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(logInSuccessIntent);

                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        String mPassword = editTextPassword.getText().toString();
        String mEmail = editTextEmail.getText().toString();
        String mName = editTextName.getText().toString();
        Integer mAge =  Integer.parseInt(editTextAge.getText().toString());
        if (view == buttonSubmit){
            createAccount(mEmail, mPassword, mName, mAge);
        }
        else if (view == buttonCancel){
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);

        }

    }
}
