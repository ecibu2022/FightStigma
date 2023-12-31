package com.example.fightstigma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<UsersModal> usersModal;

    public UsersAdapter(Context context, ArrayList<UsersModal> usersModal) {
        this.context = context;
        this.usersModal = usersModal;
    }

    @NonNull
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
        return new UsersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.MyViewHolder holder, int position) {
        UsersModal users=usersModal.get(position);
        holder.username.setText(users.getUsername());
        holder.email.setText(users.getEmail());
    }

    @Override
    public int getItemCount() {
        return usersModal.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username, email;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            email=itemView.findViewById(R.id.email);
        }
    }
}
