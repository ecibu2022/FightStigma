package com.example.fightstigma;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextInputEditText userEmail;
    Button reset;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userEmail=findViewById(R.id.email);
        reset=findViewById(R.id.reset);
        mAuth=FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String Email = userEmail.getText().toString().trim();

               if (Email.isEmpty()) {
                   userEmail.setError("Email is required");
                   userEmail.requestFocus();
                   return;
               } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                   userEmail.setError("Enter a valid email address");
                   userEmail.requestFocus();

               }else {
                   mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               Toast.makeText(ForgotPassword.this, "Please check your email to reset your password", Toast.LENGTH_LONG).show();
                               startActivity(new Intent(ForgotPassword.this, Login.class));
                           } else {
                               Toast.makeText(ForgotPassword.this, "Error failed try again later", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

               }
            }
        });

    }
}
