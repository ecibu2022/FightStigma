package com.example.fightstigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Counselor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    CounselorHomeFragment doctorHomeragment=new CounselorHomeFragment();
    CounselorPostFragment counselorPostFragment =new CounselorPostFragment();
    CounselorAppointmentsFragment counselorAppointmentsFragment =new CounselorAppointmentsFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, doctorHomeragment)
                    .commit();
            return true;
        }else if(item.getItemId() == R.id.post) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, counselorPostFragment)
                    .commit();
            return true;
        }else if(item.getItemId() == R.id.appointments) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, counselorAppointmentsFragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.doctors_options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logging Out.");
            builder.setMessage("Please confirm logging out?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Counselor.this, Login.class));
                    finish();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Counselor.this, "Logout cancelled", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();

        }

        return true;
    }


}