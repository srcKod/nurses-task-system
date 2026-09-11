package com.example.enursejobs.db.EntityLocal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.PatientEntityRemote;
import com.example.enursejobs.db.model.Patient;

@Entity(tableName = "t_patient")
public class PatientEntity implements Patient
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "room_photo_path")
    String roomPhotoPath;
    @ColumnInfo(name = "is_stopped")
    int isStopped;

    @Ignore
    public PatientEntity(){}
    public PatientEntity(String name, String roomPhotoPath, int isStopped)
    {
        this.name = name;
        this.roomPhotoPath = roomPhotoPath;
        this.isStopped = isStopped;
    }

    public PatientEntity(@NonNull PatientEntity patientEntity)
    {
        this.id = patientEntity.id;
        this.name = patientEntity.name;
        this.roomPhotoPath = patientEntity.roomPhotoPath;
        this.isStopped = patientEntity.isStopped;
    }

    public PatientEntity(@NonNull PatientEntityRemote patientEntityRemote)
    {
        this.id = patientEntityRemote.getId();
        this.name = patientEntityRemote.getName();
        this.roomPhotoPath = patientEntityRemote.getRoomPhotoPath();
        this.isStopped = patientEntityRemote.getIsStopped();
    }

    @Override
    public int getId() {return this.id;}
    @Override
    public void setId(int id) {this.id = id;}
    @Override
    public String getName() {return this.name;}
    @Override
    public void setName(String name) {this.name = name;}
    @Override
    public String getRoomPhotoPath() {return this.roomPhotoPath;}
    @Override
    public void setRoomPhotoPath(String roomPhotoPath) {this.roomPhotoPath = roomPhotoPath;}
    @Override
    public int getIsStopped() {return this.isStopped;}
    @Override
    public void setIsStopped(int is_stopped) {this.isStopped = is_stopped;}
}
