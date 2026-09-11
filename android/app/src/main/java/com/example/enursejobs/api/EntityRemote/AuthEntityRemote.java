package com.example.enursejobs.api.EntityRemote;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.AuthEntity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AuthEntityRemote
{
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;

    public AuthEntityRemote(){}
    public AuthEntityRemote(String email, String password)
    {
        this.email=email;
        this.password=password;
    }
    public AuthEntityRemote(@NonNull AuthEntityRemote auth)
    {
        this.email=auth.email;
        this.password=auth.password;
    }

    public AuthEntityRemote(@NonNull AuthEntity auth)
    {
        this.email=auth.getEmail();
        this.password=auth.getPassword();
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
