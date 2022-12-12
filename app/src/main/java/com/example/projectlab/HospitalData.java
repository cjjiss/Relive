package com.example.projectlab;

public class HospitalData {
    String nameh,emailh, addressh, phoneh;

    public HospitalData(String hospital, String nameh, String emailh, String addressh, String phoneh){

    }

    public HospitalData(String nameh, String emailh, String addressh,String phoneh) {
        this.nameh = nameh;
        this.emailh = emailh;
        this.addressh = addressh;
        this.phoneh = phoneh;

    }


    public String getName() {
        return nameh;
    }

    public void setName(String username) {
        this.nameh = nameh;
    }

    public String getEmail() {
        return emailh;
    }

    public void setEmail(String dob) {
        this.emailh = emailh;
    }

    public String getAddress() {
        return addressh;
    }

    public void setAddress(String dob) {
        this.addressh = addressh;
    }

    public String getPhone() { return phoneh;}

    public void setPhone(String contact) {
        this.phoneh = phoneh;
    }


}

