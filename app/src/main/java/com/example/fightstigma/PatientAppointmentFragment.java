package com.example.fightstigma;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentFragment extends Fragment {
    Button appointment;
    private RecyclerView appointments;
    private DatabaseReference databaseReference, usersRef;
    private List<AppointmentsModal> appointmentsModals;
    private AppointmentsAdapter appointmentsAdapter;

    public PatientAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_appointment, container, false);

        appointment=view.findViewById(R.id.appointment);

        appointments = view.findViewById(R.id.appointments);
        appointments.setHasFixedSize(true);
        appointments.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentsModals = new ArrayList<>();
        appointmentsAdapter = new AppointmentsAdapter(getContext(), (ArrayList<AppointmentsModal>) appointmentsModals);
        appointments.setAdapter(appointmentsAdapter);

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Appointments");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        usersRef=FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String UserID=snapshot.child("userID").getValue(String.class);

                databaseReference.orderByChild("patientID").equalTo(UserID).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        appointmentsModals.clear(); // Clear previous data
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            AppointmentsModal myPosts = itemSnapshot.getValue(AppointmentsModal.class);
                            appointmentsModals.add(myPosts);
                        }
                        appointmentsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatientSendAppointment bottomSheetDialogFragment = new PatientSendAppointment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                bottomSheetDialogFragment.show(transaction, bottomSheetDialogFragment.getTag());
            }
        });

        return view;
    }

}