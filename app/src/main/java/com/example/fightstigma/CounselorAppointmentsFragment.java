package com.example.fightstigma;

import static com.example.fightstigma.CounselorAppointmentsAdapter.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CounselorAppointmentsFragment extends Fragment {
    private RecyclerView appointments;
    private DatabaseReference databaseReference, usersRef;
    private List<AppointmentsModal> appointmentsModals;
    private CounselorAppointmentsAdapter appointmentsAdapter;
    String Username;

    public CounselorAppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_counselor_appointments, container, false);

        appointments = view.findViewById(R.id.appointments);
        appointments.setHasFixedSize(true);
        appointments.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentsModals = new ArrayList<>();
        appointmentsAdapter = new CounselorAppointmentsAdapter(getContext(), (ArrayList<AppointmentsModal>) appointmentsModals);
        appointments.setAdapter(appointmentsAdapter);

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Appointments");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=snapshot.child("username").getValue(String.class);

                databaseReference.orderByChild("counselor").equalTo(userName).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d("CounselorAppointments", "Snapshot exists: " + snapshot.exists());

                        appointmentsModals.clear(); // Clear previous data
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            AppointmentsModal myPosts = itemSnapshot.getValue(AppointmentsModal.class);
                            appointmentsModals.add(myPosts);
                        }
                        appointmentsAdapter.notifyDataSetChanged();

                        if (appointmentsModals.isEmpty()) {
                            Toast.makeText(getContext(), "No appointments for " + userName, Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("CounselorAppointments", "Database error: " + error.getMessage());
                        progressDialog.dismiss();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


}
