package com.example.fightstigma;

public class AppointmentsModal {
    String id, patientID, username, counselor, counselorID, type, location, date, time, phone;

    public AppointmentsModal() {
    }

    public AppointmentsModal(String id, String patientID, String username, String counselor, String counselorID, String type, String location, String date, String time, String phone) {
        this.id = id;
        this.patientID=patientID;
        this.username = username;
        this.counselor = counselor;
        this.counselorID=counselorID;
        this.type = type;
        this.location = location;
        this.date = date;
        this.time = time;
        this.phone=phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCounselor() {
        return counselor;
    }

    public void setCounselor(String counselor) {
        this.counselor = counselor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getCounselorID() {
        return counselorID;
    }

    public void setCounselorID(String counselorID) {
        this.counselorID = counselorID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
