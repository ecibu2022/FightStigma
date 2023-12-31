package com.example.fightstigma;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatientSendAppointment extends BottomSheetDialogFragment  implements AdapterView.OnItemSelectedListener {
    TextInputEditText username, type, location, date, time, phone;
    Spinner doctors;
    Button submit;
    String cName, selectedCounselorID;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_appointment_layout, container, false);

        doctors = view.findViewById(R.id.doctors);
        doctors.setOnItemSelectedListener(this);

        fetchDoctorsFromDatabase();

        username=view.findViewById(R.id.username);
        type=view.findViewById(R.id.type);
        location=view.findViewById(R.id.location);
        date=view.findViewById(R.id.date);
        time=view.findViewById(R.id.time);
        phone=view.findViewById(R.id.phone);
        submit=view.findViewById(R.id.submit);

        databaseReference=FirebaseDatabase.getInstance().getReference("appointments");
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Sending appointment");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimePicker();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=databaseReference.push().getKey();
                String patientID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                String Username=username.getText().toString();
                String Counselor=cName;
                String counselorID=selectedCounselorID;
                String TYpe=type.getText().toString();
                String Location=location.getText().toString();
                String Date=date.getText().toString();
                String Time=time.getText().toString();
                String Phone=phone.getText().toString();

                if (Username.isEmpty() || Counselor.isEmpty() || TYpe.isEmpty() || Location.isEmpty() || Date.isEmpty() || Time.isEmpty() ){
                    Toast.makeText(getContext(), "PLease fill all the fields***", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.show();
                    AppointmentsModal appointmentsModal=new AppointmentsModal(id, patientID, Username, Counselor, counselorID, TYpe, Location, Date, Time, Phone);
                    databaseReference.child(id).setValue(appointmentsModal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Appointment Sent Successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Error try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Error try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        return view;
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        date.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = DateFormat.format("h:mm a", new Date(0, 0, 0, hourOfDay, minute)).toString();
                        time.setText(selectedTime);
                    }
                },
                hour, minute, false
        );

        // Show the time picker dialog
        timePickerDialog.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cName=String.valueOf(parent.getItemAtPosition(position));
        Toast.makeText(getContext(), "Selected Counselor: " + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void fetchDoctorsFromDatabase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("role").equalTo("counselor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> doctorsList = new ArrayList<>();

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Assuming each user has a field "name" with the doctor's name
                        String doctorName = userSnapshot.child("username").getValue(String.class);
                        selectedCounselorID=userSnapshot.child("userID").getValue(String.class);
                        if (doctorName != null) {
                            doctorsList.add(doctorName);
                        }
                    }

                    // Populate the AutoCompleteTextView with the retrieved doctors
                    populateDoctorsAutoComplete(doctorsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void populateDoctorsAutoComplete(List<String> doctorsList) {
        // Create an ArrayAdapter using the retrieved list of doctors
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, doctorsList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the doctor's AutoCompleteTextView
        doctors.setAdapter(adapter);
    }

}
