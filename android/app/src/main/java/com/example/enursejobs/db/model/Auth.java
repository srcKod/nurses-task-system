package com.example.enursejobs.db.model;

public interface Auth
{
    int getId();
    void setId(int id);
    int getSeq();
    void setSeq(int seq);
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
    String getPassword();
    void setPassword(String password);
}
