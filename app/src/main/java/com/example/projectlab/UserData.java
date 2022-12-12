package com.example.projectlab;

public class UserData {
    String name,blood, phone, address,email;

    public UserData() {
    }

    public UserData(String name, String blood, String phone,String address,String email) {
        this.name = name;
        this.blood = blood;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = name;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String dob) {
        this.blood = blood;
    }

    public String getPhone() { return phone;}

    public void setPhone(String contact) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String dob) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String dob) {
        this.email = email;
    }
}

