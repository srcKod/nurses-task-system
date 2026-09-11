package com.example.enursejobs.db.EntityLocal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;
import com.example.enursejobs.db.model.Nurse;

@Entity(tableName = "t_nurse")
public class NurseEntity implements Nurse
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name= "id")
    int id;
    @ColumnInfo(name= "name")
    String name;
    @ColumnInfo(name= "email")
    String email;
    @ColumnInfo(name= "password")
    String password;
    @ColumnInfo(name= "gender")
    int gender;
    @ColumnInfo(name= "phone")
    String phone;
    @ColumnInfo(name= "is_resigned")
    int isResigned;
    @ColumnInfo(name= "is_admin")
    int isAdmin;
    @ColumnInfo(name= "fcm_token")
    String fcmToken;

    @Ignore
    public NurseEntity(){}
    public NurseEntity(int id, String name, String email,String password,
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

    public NurseEntity(@NonNull NurseEntity nurseEntity)
    {
        this.id = nurseEntity.id;
        this.name = nurseEntity.name;
        this.email = nurseEntity.email;
        this.password=nurseEntity.password;
        this.gender = nurseEntity.gender;
        this.phone = nurseEntity.phone;
        this.isResigned = nurseEntity.isResigned;
        this.isAdmin = nurseEntity.isAdmin;
        this.fcmToken = nurseEntity.fcmToken;
    }

    public NurseEntity(@NonNull NurseEntityRemote nurseEntityRemote)
    {
        this.id = nurseEntityRemote.getId();
        this.name = nurseEntityRemote.getName();
        this.email = nurseEntityRemote.getEmail();
        this.password=nurseEntityRemote.getPassword();
        this.gender = nurseEntityRemote.getGender();
        this.phone = nurseEntityRemote.getPhone();
        this.isResigned = nurseEntityRemote.getIsResigned();
        this.isAdmin = nurseEntityRemote.getIsAdmin();
        this.fcmToken = nurseEntityRemote.getFcmToken();
    }

    @Override
    public int getId(){return this.id;}
    @Override
    public void setId(int id){this.id = id;}
    @Override
    public String getName(){return this.name;}
    @Override
    public void setName(String name) { this.name = name;}
    @Override
    public String getEmail(){return this.email;}
    @Override
    public void setEmail(String email){this.email = email;}
    @Override
    public String getPassword(){return this.password;}
    @Override
    public void setPassword(String password){this.password = password;}
    @Override
    public int getGender(){return this.gender;}
    @Override
    public void setGender(int gender){this.gender = gender;}
    @Override
    public String getPhone(){return this.phone;}
    @Override
    public void setPhone(String phone){this.phone = phone;}
    @Override
    public int getIsResigned(){return this.isResigned;}
    @Override
    public void setIsResigned(int isResigned){this.isResigned = isResigned;}
    @Override
    public int getIsAdmin(){return this.isAdmin;}
    @Override
    public void setIsAdmin(int isAdmin){this.isAdmin = isAdmin;}
    @Override
    public String getFcmToken() {return this.fcmToken;}
    @Override
    public void setFcmToken(String fcmToken) {this.fcmToken = fcmToken;}

}
