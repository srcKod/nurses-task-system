package com.example.enursejobs.api.EntityRemote;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.PatientEntity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class PatientEntityRemote
{
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("room_photo_path")
    String roomPhotoPath;
    @SerializedName("is_stopped")
    int isStopped;

    public PatientEntityRemote(){}
    public PatientEntityRemote(String name, String roomPhotoPath, int isStopped)
    {
        this.name = name;
        this.roomPhotoPath = roomPhotoPath;
        this.isStopped = isStopped;
    }
    public PatientEntityRemote(@NonNull PatientEntityRemote patientEntityRemote)
    {
        this.id = patientEntityRemote.id;
        this.name = patientEntityRemote.name;
        this.roomPhotoPath = patientEntityRemote.roomPhotoPath;
        this.isStopped = patientEntityRemote.isStopped;
    }

    public PatientEntityRemote(@NonNull PatientEntity patientEntity)
    {
        this.id = patientEntity.getId();
        this.name = patientEntity.getName();
        this.roomPhotoPath = patientEntity.getRoomPhotoPath();
        this.isStopped = patientEntity.getIsStopped();
    }

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}
    public String getRoomPhotoPath() {return this.roomPhotoPath;}
    public void setRoomPhotoPath(String roomPhotoPath) {this.roomPhotoPath = roomPhotoPath;}
    public int getIsStopped() {return this.isStopped;}
    public void setIsStopped(int is_stopped) {this.isStopped = is_stopped;}
}
