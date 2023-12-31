package com.example.fightstigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Patient extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    View headerView;
    TextView full_name;
    DatabaseReference usersRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView=findViewById(R.id.nav_view);
        headerView=navigationView.getHeaderView(0);
        full_name=headerView.findViewById(R.id.full_name);

        usersRef= FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        currentUser=firebaseUser.getUid();

        usersRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username=snapshot.child("username").getValue(String.class);
                full_name.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set the title for the action bar
        getSupportActionBar().setTitle("Home");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PatientHomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.home);
        }
    }

    @Override
    public boolean onNavigationItemSelected (MenuItem item){
        if (item.getItemId() == R.id.home){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PatientHomeFragment())
                    .commit();
            getSupportActionBar().setTitle("Home");
        }else if (item.getItemId() == R.id.post){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PatientPostFragment())
                    .commit();
            getSupportActionBar().setTitle("Post Information");
        } else if (item.getItemId() == R.id.appointments){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PatientAppointmentFragment())
                    .commit();
            getSupportActionBar().setTitle("Appointments");
        } else if (item.getItemId() == R.id.logout) {
            logoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logoutDialog () {
        AlertDialog.Builder logout = new AlertDialog.Builder(this);
        logout.setTitle("Logging Out?");
        logout.setMessage("Please Confirm!");
        logout.setCancelable(false);
        logout.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                    mAuth.signOut();
                startActivity(new Intent(Patient.this, MainActivity.class));
                finish();
            }
        });
        logout.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Patient.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        logout.show(); // Show the AlertDialog
    }


}