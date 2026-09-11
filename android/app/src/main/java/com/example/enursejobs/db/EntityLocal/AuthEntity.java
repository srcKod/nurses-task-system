package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.AuthEntityRemote;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;
import com.example.enursejobs.db.model.Auth;

import org.checkerframework.checker.nullness.qual.NonNull;

@Entity(tableName = "t_auth")
public class AuthEntity implements Auth
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "seq")
    int seq;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "password")
    String password;

    @Ignore
    public AuthEntity(){}
    public AuthEntity(String email, String password)
    {
        this.id=0;
        this.seq = 0;
        this.email="";
        this.email=email;
        this.password=password;
    }
    @Ignore
    public AuthEntity(int id, int seq, String name, String email, String password)
    {
        this.id= id;
        this.seq = 0;
        this.name=name;
        this.email=email;
        this.password=password;
    }

    public AuthEntity(@NonNull AuthEntity auth)
    {
        this.id=auth.id;
        this.seq = 0;
        this.name=auth.name;
        this.email=auth.email;
        this.password=auth.password;
    }

    public AuthEntity(@NonNull NurseEntityRemote nurse)
    {
        this.id=nurse.getId();
        this.seq=0;
        this.name=nurse.getName();
        this.email=nurse.getEmail();
    }

    public int getId(){return this.id;}
    public void setId(int id) { this.id = id;}
    public int getSeq(){return this.seq;}
    public void setSeq(int seq){this.seq = seq;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
