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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PatientHomeFragment extends Fragment {
    private RecyclerView posts;
    private DatabaseReference databaseReference;
    private List<PostModal> postModalList;
    private PostsAdapter postsAdapter;

    public PatientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_patient_home, container, false);

        posts = view.findViewById(R.id.posts);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(getContext()));

        postModalList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), (ArrayList<PostModal>) postModalList);
        posts.setAdapter(postsAdapter);

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Posts");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postModalList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    PostModal myPosts = itemSnapshot.getValue(PostModal.class);
                    postModalList.add(myPosts);
                }
                sortPostsByDateTime(postModalList);
                postsAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
            }

        });

        return  view;
    }

    // Sorting the postModalList based on date and time (newest to oldest)
    private void sortPostsByDateTime(List<PostModal> postModalList) {
        Collections.sort(postModalList, new Comparator<PostModal>() {
            @Override
            public int compare(PostModal notice1, PostModal notice2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(notice1.getDate());
                    Date date2 = sdf.parse(notice2.getDate());
                    // Sorting in descending order (newest to oldest)
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

}