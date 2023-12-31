package com.example.fightstigma;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientPostFragment extends Fragment {
    ImageView image;
    TextInputEditText title, body;
    Button post;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    public static final int RESULT_OK = -1;

    public PatientPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_patient_post, container, false);

        image=view.findViewById(R.id.ImageView);
        title=view.findViewById(R.id.title);
        body=view.findViewById(R.id.body);
        post=view.findViewById(R.id.post);

        progressDialog= new ProgressDialog(getContext());
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title=title.getText().toString();
                String Body=body.getText().toString();
                if (!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(Body) && imageUri != null) {
                    uploadPostToFirebase(imageUri);
                }else {
                    Toast.makeText(getContext(), "Select post Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    //    Choosing Image
    private void chooseImage() {
        // Open gallery to select image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    public void uploadPostToFirebase(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please Select Post Image", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("Post Upload in Progress");
            progressDialog.setMessage("Please wait....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Get the file extension from the imageUri
            ContentResolver contentResolver = getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + extension);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String Date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
                            String Title = title.getText().toString();
                            String Body = body.getText().toString();
                            String postID=databaseReference.push().getKey();

                            PostModal post=new PostModal(postID, userID, Date, uri.toString(), Title, Body);

                            databaseReference.child(postID).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        image.setImageResource(R.drawable.common_google_signin_btn_icon_disabled);
                                        title.setText(null);
                                        body.setText(null);
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Failed try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}