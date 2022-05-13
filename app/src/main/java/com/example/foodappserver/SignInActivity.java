package com.example.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    private EditText emailTxt, passwordTxt;
    private Button signInBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        emailTxt = (EditText) findViewById(R.id.emailEditText);
        passwordTxt = (EditText) findViewById(R.id.passwordEditText);
        signInBtn = (Button) findViewById(R.id.signBtn);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reference = database.getReference("Users");



        user = FirebaseAuth.getInstance().getCurrentUser();
        user = mAuth.getCurrentUser();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(emailTxt.getText().toString(), passwordTxt.getText().toString());

            }
        });

    }

   /* private void signInUser(String toString, String toString1) {

        final ProgressDialog mDialog =new ProgressDialog(SignInActivity.this);
        mDialog.setMessage("Please waiting....");
        mDialog.show();

        final String loclaEmail =toString;
        final String localpwd =toString1;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                        mDialog.dismiss();
                        User user = key.child(loclaEmail).getValue(User.class);
                        user.setEmail(loclaEmail);
                        if (Boolean.parseBoolean(user.getIsStaff())) {
                            if (user.getPassword().equals(localpwd)) {

                            } else
                                Toast.makeText(SignInActivity.this, "wrng pass", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SignInActivity.this, " please wrng pass", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "user not please wrng pass", Toast.LENGTH_SHORT).show();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

   private void signInUser(String email, String pwrd) {
       final String localName = email;
       final String localPassword = pwrd;
       mAuth.signInWithEmailAndPassword(localName, localPassword)
               .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                   @Override
                   public void onSuccess(AuthResult authResult) {
                       if (user.getEmail() != null) {
                           reference.orderByChild(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if (snapshot.child(user.getEmail()).exists()) {
                                       User users = snapshot.child(user.getEmail()).getValue(User.class);
                                       users.setEmail(localName);
                                       if (Boolean.parseBoolean(users.getIsStaff())) {
                                           if (users.getPassword().equals(localPassword)) {
                                               startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                               finish();

                                           } else
                                               Toast.makeText(SignInActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                       } else
                                           Toast.makeText(SignInActivity.this, "PLease login with staff account", Toast.LENGTH_SHORT).show();
                                   } else {
                                       Toast.makeText(SignInActivity.this, "User does notexists", Toast.LENGTH_SHORT).show();
                                   }

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });


                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(SignInActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
           }
       });

   }
    }



