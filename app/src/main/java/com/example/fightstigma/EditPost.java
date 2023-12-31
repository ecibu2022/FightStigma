package com.example.fightstigma;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditPost extends BottomSheetDialogFragment {
    ImageView image;
    TextInputEditText title, body;
    Button edit;
    private Uri newImageUri; // Store the Uri of the new image
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_post_layout, container, false);
        image=view.findViewById(R.id.ImageView);
        title=view.findViewById(R.id.title);
        body=view.findViewById(R.id.body);
        edit=view.findViewById(R.id.edit);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        // Retrieve post data from arguments
        PostModal post = getArguments().getParcelable("post");

        if (post != null) {
            // Set initial values in EditText fields
            Glide.with(getContext()).load(post.getImageURL()).into(image);
            title.setText(post.getTitle());
            body.setText(post.getBody());
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle image selection
                // Open gallery to select image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the save button click
                String updatedTitle = title.getText().toString();
                String updatedBody = body.getText().toString();

                // Upload new image to Firebase Storage
                if (newImageUri != null) {
                    ((PostDetails) getActivity()).updatePost(post, updatedTitle, updatedBody, newImageUri);
                } else {
                    // If no new image is selected, update post without changing the image
                    ((PostDetails) getActivity()).updatePost(post, updatedTitle, updatedBody, null);
                }

                // Dismiss the bottom sheet after saving
                dismiss();

            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            Glide.with(this).load(newImageUri).into(image);
        }
    }

}
