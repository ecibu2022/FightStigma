package com.example.fightstigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    TextInputEditText username, email, password, confirm;
    Button register;
    TextView login;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm=findViewById(R.id.confirm);
        register=findViewById(R.id.register);
        login=findViewById(R.id.login);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Registration in progress");
        progressDialog.setMessage("PLease wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username=username.getText().toString();
                String Email=email.getText().toString().trim();
                String Password=password.getText().toString().trim();
                String Confirm=confirm.getText().toString().trim();
                
                if (Username.isEmpty()){
                    username.setError("Enter Username");
                    username.requestFocus();
                    return;
                } else if (Email.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Enter a valid email address");
                    email.requestFocus();
                    return;
                } else if (Password.isEmpty()) {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                } else if (Password.length() < 6) {
                    password.setError("Password should be at least 6 characters long");
                    password.requestFocus();
                    return;
                } else if (Confirm.isEmpty()) {
                    confirm.setError("Please confirm password");
                    confirm.requestFocus();
                    return;
                } else if (!Password.equals(Confirm)) {
                    confirm.setError("Password mismatched");
                    confirm.requestFocus();
                    return;
                }else{
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                UsersModal users=new UsersModal(FirebaseAuth.getInstance().getCurrentUser().getUid(), Username, Email, Password, "patient");
                                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        
    }
}