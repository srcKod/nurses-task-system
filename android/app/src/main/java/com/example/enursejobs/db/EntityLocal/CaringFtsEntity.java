package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;

import java.util.Date;

@Entity(tableName= "caring_Fts")
@Fts4(contentEntity = CaringEntity.class)
public class CaringFtsEntity
{
    @ColumnInfo(name = "nurse_name")
    String nurseName;
    @ColumnInfo(name = "caringtype_name")
    String caringtypeName;
    @ColumnInfo(name = "patient_name")
    String patientName;
    @ColumnInfo(name = "status")
    String status;
    @ColumnInfo(name = "time")
    Date time;
    @ColumnInfo(name = "description")
    String description;

    @Ignore
    public CaringFtsEntity(){}
    public CaringFtsEntity(String nurseName, String caringtypeName,String patientName,
                           String status, Date time, String description)
    {
        this.nurseName=nurseName;
        this.caringtypeName=caringtypeName;
        this.patientName=patientName;
        this.status=status;
        this.time=time;
        this.description=description;
    }
}
