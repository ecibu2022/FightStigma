package com.example.fightstigma;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PostModal> posts;

    public PostsAdapter(Context context, ArrayList<PostModal> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.MyViewHolder holder, int position) {
        PostModal myPosts=posts.get(position);

        Glide.with(context).load(myPosts.getImageURL()).into(holder.postImage);
        holder.date.setText(myPosts.getDate());
        holder.title.setText(myPosts.getTitle());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected post
                PostModal selectedPost = posts.get(position);

                // Create an Intent and pass the selected post
                Intent details = new Intent(context, PostDetails.class);
                details.putExtra("post", selectedPost);
                context.startActivity(details);
            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView postImage;
        TextView date, title;
        Button more;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage=itemView.findViewById(R.id.postImage);
            date=itemView.findViewById(R.id.date);
            title=itemView.findViewById(R.id.title);
            more=itemView.findViewById(R.id.more);
        }
    }
}
