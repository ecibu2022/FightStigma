package com.example.fightstigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetails extends AppCompatActivity {
    PostModal post;
    CircleImageView postImage;
    TextView date, title, body, num_of_likes, total_comments;
    ImageView likeImageView;
    EditText userComment;
    Button postBtn;

    private DatabaseReference likesRef,postRef, postsRef, usersRef, commentsRef;
    private FirebaseUser currentUser;
    private boolean isLikedByCurrentUser = false;
    private RecyclerView commentsRecyclerView;
    private List<Comment> comments;
    private CommentsAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Retrieve the post object from the intent
        post = getIntent().getParcelableExtra("post");

        if (post == null) {
            // Handle the case when notice is null
            Toast.makeText(this, "Error: Post is null.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postImage=findViewById(R.id.userImage);
        date=findViewById(R.id.date);
        title=findViewById(R.id.title);
        body=findViewById(R.id.body);

        likeImageView = findViewById(R.id.like);
        num_of_likes=findViewById(R.id.num_of_likes);
        total_comments=findViewById(R.id.total_comments);
        userComment=findViewById(R.id.userComment);
        postBtn=findViewById(R.id.post);
        commentsRecyclerView=findViewById(R.id.comments);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        comments=new ArrayList<>();
        commentAdapter=new CommentsAdapter(PostDetails.this, (ArrayList<Comment>) comments);
        commentsRecyclerView.setAdapter(commentAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        likesRef = FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostID());
        postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID());
        postsRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID());
        commentsRef=FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID()).child("comments");

//        Counting comments
        totalComments();

        Glide.with(this).load(post.getImageURL()).into(postImage);
        date.setText(post.getDate());
        title.setText(post.getTitle());
        body.setText(post.getBody());

        // Check if the current user is the owner of the post
        if (currentUser != null && post.getUserID().equals(currentUser.getUid())) {
            // User is the owner, show edit and delete buttons
            Button editBtn = findViewById(R.id.edit);
            Button deleteBtn = findViewById(R.id.delete);

            LinearLayout showButtons=findViewById(R.id.show);

            showButtons.setVisibility(View.VISIBLE);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPost();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePost();
                }
            });

        }

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                PostModal postModal = snapshot.getValue(PostModal.class);

                if (postModal != null) {
                    // Check if the comments field is not null
                    HashMap<String, Comment> commentsMap = postModal.getComments();
                    if (commentsMap != null) {
                        // The comments field is not null, so retrieve the comments
                        comments.clear();
                        for (String commentId : commentsMap.keySet()) {
                            Comment comment = commentsMap.get(commentId);
                            comments.add(comment);
                        }

                        // Notify the adapter that the data has changed
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database read error
            }
        });

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the total number of likes
                long likesCount = snapshot.getChildrenCount();
                // Update the like count TextView
                num_of_likes.setText(String.valueOf(likesCount)+" Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    // User not logged in, handle login or show a message
                    return;
                }

                if (isLikedByCurrentUser) {
                    // User already liked the notice, perform unlike action
                    likesRef.child(currentUser.getUid()).removeValue();
                    decrementLikeCount();
                } else {
                    // User has not liked the notice, perform like action
                    likesRef.child(currentUser.getUid()).setValue(true);
                    incrementLikeCount();
                }

                // Update the UI to reflect the change in like status
                isLikedByCurrentUser = !isLikedByCurrentUser;
                updateLikeStatus();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = userComment.getText().toString().trim();
                String userId = currentUser.getUid();
                String timeCommented = getCurrentDateTime();
                String commentId = postRef.push().getKey();

                if (commentText.isEmpty()) {
                    userComment.setError("Comment Box is Empty");
                    userComment.requestFocus();
                    return;
                }

                // Retrieve the user's profile data from Firebase
                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get the user data from the snapshot
                        String userID = snapshot.child("userID").getValue(String.class);

                        // Create a new Comment object
                        Comment newComment = new Comment(commentId, commentText, userID, timeCommented);

                        // Add the comment to the notice
                        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                PostModal posts = snapshot.getValue(PostModal.class);

                                if (posts != null) {
                                    // Initialize comments HashMap if necessary
                                    if (posts.getComments() == null) {
                                        posts.setComments(new HashMap<>());
                                    }

                                    // Add the new comment to the HashMap
                                    posts.getComments().put(commentId, newComment);

                                    // Update the notice in the database
                                    postsRef.setValue(posts)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(PostDetails.this, "Comment Sent Successfully", Toast.LENGTH_SHORT).show();
                                                    userComment.setText(null);
//                                                    Updating comments
                                                    totalComments();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PostDetails.this, "Error Failed to Post Comment " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle database read error
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
            }
        });

    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Function to update the like button UI based on user's like status
    private void updateLikeStatus() {
        if (isLikedByCurrentUser) {
            likeImageView.setImageResource(R.drawable.liked);
        } else {
            likeImageView.setImageResource(R.drawable.like);
        }
    }

    // Function to increment the likeCount in the database
    private void incrementLikeCount() {
        // Increment the likeCount in the database
        post.setLikeCount(post.getLikeCount() + 1);

        // Update the likeCount value in the "Notices" node
        postRef.child("likeCount").setValue(post.getLikeCount())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Like count successfully incremented in the database
                        Log.d("NoticeDetails", "New like count: " + post.getLikeCount());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update like count in the database
                        Toast.makeText(PostDetails.this, "Failed to update like count", Toast.LENGTH_SHORT).show();
                        // Revert the local change since the database update failed
                        post.setLikeCount(post.getLikeCount() - 1);
                        updateLikeStatus(); // Update the UI to reflect the reverted change
                    }
                });
    }

    // Function to decrement the likeCount in the database
    private void decrementLikeCount() {
        // Decrement the likeCount in the database
        post.setLikeCount(post.getLikeCount() - 1);

        // Update the likeCount value in the "Posts" node
        postRef.child("likeCount").setValue(post.getLikeCount())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Like count successfully decremented in the database
                        Log.d("NoticeDetails", "New like count: " + post.getLikeCount());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update like count in the database
                        Toast.makeText(PostDetails.this, "Failed to update like count", Toast.LENGTH_SHORT).show();
                        // Revert the local change since the database update failed
                        post.setLikeCount(post.getLikeCount() + 1);
//                        updateLikeStatus(); // Update the UI to reflect the reverted change
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Add an AuthStateListener to handle user login/logout events
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove the AuthStateListener to avoid memory leaks
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    // AuthStateListener to handle user login/logout events
    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser = firebaseAuth.getCurrentUser();
            // Check if the current user has already liked the notice after login/logout
            if (currentUser != null) {
                likesRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLikedByCurrentUser = snapshot.exists();
                        updateLikeStatus();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    };

    // Method to handle post editing
    private void editPost() {
        EditPost bottomSheetFragment = new EditPost();

        // Pass the post data to the BottomSheetFragment
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        bottomSheetFragment.setArguments(args);

        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    // Method to handle post deletion
    private void deletePost() {
        AlertDialog.Builder delete=new AlertDialog.Builder(this);
        delete.setTitle("Deleting your post");
        delete.setMessage("Are you sure? You will not recover it back");
        delete.setCancelable(false);
        delete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // For simplicity, I'll directly delete the post without confirmation
                postRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Post deleted successfully
                                Toast.makeText(PostDetails.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Finish this activity to go back to the previous activity
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete the post
                                Toast.makeText(PostDetails.this, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        delete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PostDetails.this, "Post deletion cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        delete.show();


    }

    public void updatePost(PostModal post, String updatedTitle, String updatedBody, Uri newImageUri) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostID());

        // If a new image is selected, upload it to Firebase Storage
        if (newImageUri != null) {
            uploadNewImage(postRef, updatedTitle, updatedBody, newImageUri);
        } else {
            // If no new image is selected, update post without changing the image
            updatePostWithoutImage(postRef, updatedTitle, updatedBody, post.getImageURL());
        }
    }

    private void uploadNewImage(DatabaseReference postRef, String updatedTitle, String updatedBody, Uri newImageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts");
        StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the updated image URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the post in the database with the new image URL
                        updatePostWithoutImage(postRef, updatedTitle, updatedBody, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to upload the new image
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePostWithoutImage(DatabaseReference postRef, String updatedTitle, String updatedBody, String imageURL) {
        // Update the post in the database without changing the image
        postRef.child("title").setValue(updatedTitle);
        postRef.child("body").setValue(updatedBody);
        postRef.child("imageURL").setValue(imageURL);

        // Update the UI to reflect the changes
        title.setText(updatedTitle);
        body.setText(updatedBody);
        Glide.with(this).load(imageURL).into(postImage);

        Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
    }


    //        Counting total comments for a post
    public void totalComments(){
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long commentCount=snapshot.getChildrenCount();
                total_comments.setText(String.valueOf(commentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        // Finish the activity and go back to the previous activity
        super.onBackPressed();
        finish();
    }

}