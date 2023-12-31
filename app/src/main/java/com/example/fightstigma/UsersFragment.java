package com.example.fightstigma;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private RecyclerView users;
    private DatabaseReference databaseReference;
    private List<UsersModal> usersModal;
    private UsersAdapter usersAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        users = view.findViewById(R.id.myUsers);
        users.setHasFixedSize(true);
        users.setLayoutManager(new LinearLayoutManager(getContext()));

        usersModal = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), (ArrayList<UsersModal>) usersModal);
        users.setAdapter(usersAdapter);

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Users");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                usersModal.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    UsersModal myUsers = itemSnapshot.getValue(UsersModal.class);
                    usersModal.add(myUsers);
                }
                usersAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
            }

        });

        return view;
    }
}