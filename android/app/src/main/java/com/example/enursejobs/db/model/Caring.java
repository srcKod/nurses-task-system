package com.example.enursejobs.db.model;

import java.util.Date;

public interface Caring
{
    int getId();
    void setId(int id);
    int getNurseId();
    String getNurseName();
    void setNurseId(int nurse_id);
    int getCaringtypeId();
    String getCaringtypeName();
    void setCaringtypeId(int caringtype_id);
    int getPatientId();
    String getPatientName();
    void setPatientId(int patient_id);
    String getStatus();
    String getTime();
    void setTime(String time);
    String getDescription();
    void setDescription(String description);
    String getCreatedAt();
    void setCreatedAt(String time);
    String getDeletedAt();
    void setDeletedAt(String time);
}
