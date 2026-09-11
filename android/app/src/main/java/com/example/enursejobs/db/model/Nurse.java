package com.example.enursejobs.db.model;

public interface Nurse
{
    int getId();
    void setId(int id);
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String password);
    String getPassword();
    void setPassword(String password);
    int getGender();
    void setGender(int gender);
    String getPhone();
    void setPhone(String phone);
    int getIsResigned();
    void setIsResigned(int is_resigned);
    int getIsAdmin();
    void setIsAdmin(int is_admin);
    String getFcmToken();
    void setFcmToken(String fcm_token);
}
