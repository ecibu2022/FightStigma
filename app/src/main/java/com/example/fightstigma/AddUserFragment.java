package com.example.fightstigma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserFragment extends Fragment {
    TextInputEditText username, email, password, confirm;
    CheckBox patient, doctor;
    Button addUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String Role;


    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_user, container, false);

        username=view.findViewById(R.id.username);
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        confirm=view.findViewById(R.id.confirm);
        addUser=view.findViewById(R.id.add_user);
        patient=view.findViewById(R.id.patient);
        doctor=view.findViewById(R.id.doctor);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Registration in progress");
        progressDialog.setMessage("PLease wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username=username.getText().toString();
                String Email=email.getText().toString().trim();
                String Password=password.getText().toString().trim();
                String Confirm=confirm.getText().toString().trim();

                boolean isPatientChecked = patient.isChecked();

                if (isPatientChecked) {
                    Role="patient";
                } else {
                    doctor.setEnabled(false);
                }

                boolean isDoctorChecked = doctor.isChecked();

                if (isDoctorChecked) {
                    Role="counselor";
                } else {
                    patient.setEnabled(false);
                }

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
                                UsersModal users=new UsersModal(FirebaseAuth.getInstance().getCurrentUser().getUid(), Username, Email, Password, Role);
                                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "User Added Successfully", Toast.LENGTH_SHORT).show();
                                        username.setText(null);
                                        email.setText(null);
                                        password.setText(null);
                                        confirm.setText(null);
                                        patient.setChecked(false);
                                        doctor.setChecked(false);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }


        });



        return view;
    }
}