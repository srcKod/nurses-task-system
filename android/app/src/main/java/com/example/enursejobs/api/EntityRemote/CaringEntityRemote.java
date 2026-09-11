package com.example.enursejobs.api.EntityRemote;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.CaringEntity;

import java.util.Date;
import java.util.Objects;

public class CaringEntityRemote
{
    @SerializedName("id")
    int id;
    @SerializedName("nurse_id")
    int nurseId;
    @SerializedName("nurse_name")
    String nurseName;
    @SerializedName("caringtype_id")
    int caringtypeId;
    @SerializedName("caringtype_name")
    String caringtypeName;
    @SerializedName("patient_id")
    int patientId;
    @SerializedName("patient_name")
    String patientName;
    @SerializedName("status")
    String status;
    @SerializedName("time")
    String time;
    @SerializedName("description")
    String description;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("deleted_at")
    String deletedAt;

    public CaringEntityRemote(){}
    public CaringEntityRemote(int id, int nurseId, String nurseName, int caringtypeId, String caringtypeName,
                              int patientId, String patientName, String time, String description, String createdAt, String deletedAt)
    {
        this.id = id;
        this.nurseId = nurseId;
        this.nurseName = nurseName;
        this.caringtypeId = caringtypeId;
        this.caringtypeName = caringtypeName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.time = time;
        this.description = description;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public CaringEntityRemote(@NonNull CaringEntityRemote caringEntityRemote)
    {
        this.time = caringEntityRemote.time;
        this.nurseId = caringEntityRemote.nurseId;
        this.nurseName = caringEntityRemote.nurseName;
        this.caringtypeId = caringEntityRemote.caringtypeId;
        this.caringtypeName = caringEntityRemote.caringtypeName;
        this.patientId = caringEntityRemote.patientId;
        this.patientName = caringEntityRemote.patientName;
        this.description = caringEntityRemote.description;
        this.createdAt = caringEntityRemote.createdAt;
        this.deletedAt = caringEntityRemote.deletedAt;
    }

    public CaringEntityRemote(@NonNull CaringEntity caringEntity)
    {
        this.time = caringEntity.getTime();
        this.nurseId = caringEntity.getNurseId();
        this.caringtypeId = caringEntity.getCaringtypeId();
        this.patientId = caringEntity.getPatientId();
        this.description = caringEntity.getDescription();
        if(Objects.equals(caringEntity.getStatus(), "Pending")){
            this.status = "0";
        }
        else if(Objects.equals(caringEntity.getStatus(), "Finished")){
            this.status = "1";
        }
    }

    public int getId() {return this.id;}
    public void setId(int id) {this.id =id;}
    public int getNurseId() {return this.nurseId;}
    public void setNurseId(int nurseId) {this.nurseId = nurseId;}
    public String getNurseName() {return this.nurseName;}
    public int getCaringtypeId() {return this.caringtypeId;}
    public void setCaringtypeId(int caringtypeId) {this.caringtypeId =caringtypeId;}
    public String getCaringtypeName() {return this.caringtypeName;}
    public int getPatientId() {return this.patientId;}
    public void setPatientId(int patientId) {this.patientId = patientId;}
    public String getPatientName() {return this.patientName;}
    public String getStatus() {return this.status;}
    public String getTime() {return time;}
    public void setTime(String time) {this.time = time;}
    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description=description;}
    public String getCreatedAt() {return createdAt;}
    public void setCreatedAt(String time) {this.createdAt = createdAt;}
    public String getDeletedAt() {return deletedAt;}
    public void setDeletedAt(String time) {this.deletedAt = deletedAt;}
}
