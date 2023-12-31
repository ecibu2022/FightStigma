package com.example.fightstigma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CounselorAppointmentsAdapter extends RecyclerView.Adapter<CounselorAppointmentsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<AppointmentsModal> appointmentsModals;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    public CounselorAppointmentsAdapter(Context context, ArrayList<AppointmentsModal> appointmentsModals) {
        this.context = context;
        this.appointmentsModals = appointmentsModals;
    }

    @NonNull
    @Override
    public CounselorAppointmentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counselor_approve_appointment, parent, false);
        return new CounselorAppointmentsAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CounselorAppointmentsAdapter.MyViewHolder holder, int position) {
        AppointmentsModal appointmentsModal = appointmentsModals.get(position);
        holder.username.setText(appointmentsModal.getUsername());
        holder.counselor.setText(appointmentsModal.getCounselor());
        holder.type.setText(appointmentsModal.getType());
        holder.location.setText(appointmentsModal.getLocation());
        holder.date.setText(appointmentsModal.getDate());
        holder.time.setText(appointmentsModal.getTime());
        holder.phone.setText(appointmentsModal.getPhone());

        holder.approve.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String phoneNumber = appointmentsModal.getPhone();

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            openMessagingApp(phoneNumber, "Your appointment has been approved!");
            holder.approve.setText(R.string.approved);
            Toast.makeText(context, "Appointment approved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Patient's phone number not available", Toast.LENGTH_SHORT).show();
        }
    }
});


    }
    
    
    
private void openMessagingApp(String phoneNumber, String message) {
    Uri uri = Uri.parse("smsto:" + phoneNumber);
    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
    intent.putExtra("sms_body", message);

    // Check if there's an app to handle this intent
    if (intent.resolveActivity(context.getPackageManager()) != null) {
        context.startActivity(intent);
    } else {
        Toast.makeText(context, "No messaging app available", Toast.LENGTH_SHORT).show();
    }
}

    private void saveApprovalState(String appointmentId) {
        SharedPreferences preferences = context.getSharedPreferences("ApprovalPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(appointmentId, true); // Set to true when approved
        editor.apply();
    }

    private boolean isApproved(String appointmentId) {
        SharedPreferences preferences = context.getSharedPreferences("ApprovalPrefs", Context.MODE_PRIVATE);
        return preferences.getBoolean(appointmentId, false); // Default is false if not approved
    }


    @Override
    public int getItemCount() {
        return appointmentsModals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, counselor, type, location, date, time, phone;
        Button approve;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            counselor = itemView.findViewById(R.id.counselor);
            type = itemView.findViewById(R.id.type);
            location = itemView.findViewById(R.id.location);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            phone = itemView.findViewById(R.id.phone);
            approve = itemView.findViewById(R.id.approve);
        }

    }
}
