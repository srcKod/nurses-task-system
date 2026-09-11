package com.example.enursejobs.db.EntityLocal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.CaringEntityRemote;
import com.example.enursejobs.db.model.Caring;

@Entity(tableName= "t_caring")
public class CaringEntity implements Caring
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "nurse_id")
    int nurseId;
    @ColumnInfo(name = "nurse_name")
    String nurseName;
    @ColumnInfo(name = "caringtype_id")
    int caringtypeId;
    @ColumnInfo(name = "caringtype_name")
    String caringtypeName;
    @ColumnInfo(name = "patient_id")
    int patientId;
    @ColumnInfo(name = "patient_name")
    String patientName;
    @ColumnInfo(name = "status")
    String status;
    @ColumnInfo(name = "time")
    String time;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "created_at")
    String createdAt;
    @ColumnInfo(name = "deleted_at")
    String deletedAt;

    @Ignore
    public CaringEntity(){}
    public CaringEntity(int id, int nurseId, String nurseName,
                        int caringtypeId, String caringtypeName,
                        int patientId, String patientName, String status,
                        String time, String description, String createdAt, String deletedAt)
    {
        this.id=id;
        this.nurseId = nurseId;
        this.nurseName=nurseName;
        this.caringtypeId =caringtypeId;
        this.caringtypeName=caringtypeName;
        this.patientId = patientId;
        this.patientName=patientName;
        this.status=status;
        this.time=time;
        this.description=description;
        this.createdAt=createdAt;
        this.deletedAt=deletedAt;
    }

    public CaringEntity(@NonNull CaringEntity caringEntity)
    {
        this.id=caringEntity.id;
        this.nurseId = caringEntity.getNurseId();
        this.nurseName=caringEntity.getNurseName();
        this.caringtypeId = caringEntity.getCaringtypeId();
        this.caringtypeName=caringEntity.getCaringtypeName();
        this.patientId = caringEntity.getPatientId();
        this.patientName=caringEntity.getPatientName();
        this.status=caringEntity.getStatus();
        this.time=caringEntity.getTime();
        this.description=caringEntity.getDescription();
        this.createdAt=caringEntity.createdAt;
        this.deletedAt=caringEntity.deletedAt;
    }

    public CaringEntity(@NonNull CaringEntityRemote caringEntityRemote)
    {
        this.id=caringEntityRemote.getId();
        this.nurseId = caringEntityRemote.getNurseId();
        this.nurseName=caringEntityRemote.getNurseName();
        this.caringtypeId = caringEntityRemote.getCaringtypeId();
        this.caringtypeName=caringEntityRemote.getCaringtypeName();
        this.patientId = caringEntityRemote.getPatientId();
        this.patientName=caringEntityRemote.getPatientName();
        this.status=caringEntityRemote.getStatus();
        this.time=caringEntityRemote.getTime();
        this.description=caringEntityRemote.getDescription();
        this.createdAt=caringEntityRemote.getCreatedAt();
        this.deletedAt=caringEntityRemote.getDeletedAt();
    }

    @Override
    public int getId() {return this.id;}
    @Override
    public void setId(int id) {this.id =id;}
    @Override
    public int getNurseId() {return this.nurseId;}
    @Override
    public void setNurseId(int nurseId) {this.nurseId = nurseId;}
    public String getNurseName() {return this.nurseName;}
    @Override
    public int getCaringtypeId() {return this.caringtypeId;}
    @Override
    public void setCaringtypeId(int caringtypeId) {this.caringtypeId =caringtypeId;}
    public String getCaringtypeName() {return this.caringtypeName;}

    @Override
    public int getPatientId() {return this.patientId;}
    @Override
    public void setPatientId(int patientId) {this.patientId = patientId;}
    public String getPatientName() {return this.patientName;}
    public String getStatus() {return this.status;}
    @Override
    public String getTime() {return time;}
    @Override
    public void setTime(String time) {this.time=time;}
    @Override
    public String getDescription() {return this.description;}
    @Override
    public void setDescription(String description) {this.description=description;}
    @Override
    public String getCreatedAt() {return createdAt;}
    @Override
    public void setCreatedAt(String time) {this.createdAt = createdAt;}
    @Override
    public String getDeletedAt() {return deletedAt;}
    @Override
    public void setDeletedAt(String time) {this.deletedAt = deletedAt;}
}
