package com.example.fightstigma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<AppointmentsModal> appointmentsModals;

    public AppointmentsAdapter(Context context, ArrayList<AppointmentsModal> appointmentsModals) {
        this.context = context;
        this.appointmentsModals = appointmentsModals;
    }

    @NonNull
    @Override
    public AppointmentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_layout, parent, false);
        return new AppointmentsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsAdapter.MyViewHolder holder, int position) {
        AppointmentsModal appointmentsModal=appointmentsModals.get(position);
        holder.username.setText(appointmentsModal.getUsername());
        holder.counselor.setText(appointmentsModal.getCounselor());
        holder.type.setText(appointmentsModal.getType());
        holder.location.setText(appointmentsModal.getLocation());
        holder.date.setText(appointmentsModal.getDate());
        holder.time.setText(appointmentsModal.getTime());
        holder.phone.setText(appointmentsModal.getPhone());
    }

    @Override
    public int getItemCount() {
        return appointmentsModals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, counselor, type, location, date, time, phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            counselor=itemView.findViewById(R.id.counselor);
            type=itemView.findViewById(R.id.type);
            location=itemView.findViewById(R.id.location);
            date=itemView.findViewById(R.id.date);
            time=itemView.findViewById(R.id.time);
            phone=itemView.findViewById(R.id.phone);
        }
    }
}
