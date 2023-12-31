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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    TextInputEditText email, password;
    TextView forgot, register;
    Button sigin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
//        Getting Current User
        currentUser = firebaseAuth.getCurrentUser();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgot = findViewById(R.id.forgot);
        register = findViewById(R.id.register);
        sigin = findViewById(R.id.signin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Counselor.class));
                finish();
            }
        });

        //        Login Button
        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (Email.isEmpty()) {
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
                }
                progressDialog.show();
                // Authenticate user with Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            String userId = currentUser.getUid();

                            // Retrieve user data from Realtime Database
                            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String role = dataSnapshot.child("role").getValue(String.class);

                                        if (role != null && role.equals("patient")) {
                                            // Update the user's email in Firebase Authentication
                                            currentUser.updateEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Update the user's email in Realtime Database
                                                        databaseReference.child(userId).child("email").setValue(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(Login.this, Patient.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        } else if (role != null && role.equals("admin")) {
                                            // Update the user's email in Firebase Authentication
                                            currentUser.updateEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Update the user's email in Realtime Database
                                                        databaseReference.child(userId).child("email").setValue(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(Login.this, Admin.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        } else if (role != null && role.equals("counselor")) {
                                            // Update the user's email in Firebase Authentication
                                            currentUser.updateEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Update the user's email in Realtime Database
                                                        databaseReference.child(userId).child("email").setValue(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(Login.this, Counselor.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(Login.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(Login.this, "Login Failed. Please try again", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Login.this, "Login Failed. Please try again", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, "Login Failed. Please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        });

    }
}