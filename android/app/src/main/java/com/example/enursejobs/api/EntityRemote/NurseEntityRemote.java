package com.example.enursejobs.api.EntityRemote;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

public class NurseEntityRemote
{
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("gender")
    int gender;
    @SerializedName("phone")
    String phone;
    @SerializedName("is_resigned")
    int isResigned;
    @SerializedName("is_admin")
    int isAdmin;
    @SerializedName("fcm_token")
    String fcmToken;

    public NurseEntityRemote(){}
    public NurseEntityRemote(int id, String name, String email,String password,
                             int gender, String phone, int isResigned, int isAdmin,
                             String fcmToken)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password=password;
        this.gender = gender;
        this.phone = phone;
        this.isResigned = isResigned;
        this.isAdmin = isAdmin;
        this.fcmToken = fcmToken;
    }

    public NurseEntityRemote(@NonNull NurseEntityRemote nurseEntityRemote)
    {
        this.id = nurseEntityRemote.id;
        this.name = nurseEntityRemote.name;
        this.email = nurseEntityRemote.email;
        this.password=nurseEntityRemote.password;
        this.gender = nurseEntityRemote.gender;
        this.phone = nurseEntityRemote.phone;
        this.isResigned = nurseEntityRemote.isResigned;
        this.isAdmin = nurseEntityRemote.isAdmin;
        this.fcmToken = nurseEntityRemote.fcmToken;
    }

    public NurseEntityRemote(@NonNull NurseEntity nurseEntity)
    {
        this.id = nurseEntity.getId();
        this.name = nurseEntity.getName();
        this.email = nurseEntity.getEmail();
        this.password = nurseEntity.getPassword();
        this.gender = nurseEntity.getGender();
        this.phone = nurseEntity.getPhone();
        this.isResigned = nurseEntity.getIsResigned();
        this.isAdmin = nurseEntity.getIsAdmin();
        this.fcmToken = nurseEntity.getFcmToken();
    }

    public int getId(){return this.id;}
    public void setId(int id){this.id = id;}
    public String getName(){return this.name;}
    public void setName(String name) { this.name = name;}
    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email = email;}
    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}
    public int getGender(){return this.gender;}
    public void setGender(int gender){this.gender = gender;}
    public String getPhone(){return this.phone;}
    public void setPhone(String phone){this.phone = phone;}
    public int getIsResigned(){return this.isResigned;}
    public void setIsResigned(int isResigned){this.isResigned = isResigned;}
    public int getIsAdmin(){return this.isAdmin;}
    public void setIsAdmin(int isAdmin){this.isAdmin = isAdmin;}
    public String getFcmToken() {return this.fcmToken;}
    public void setFcmToken(String fcmToken) {this.fcmToken = fcmToken;}
}
