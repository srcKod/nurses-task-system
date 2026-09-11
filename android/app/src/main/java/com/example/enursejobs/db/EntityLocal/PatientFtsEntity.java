package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;

@Entity(tableName = "patient_Fts")
@Fts4(contentEntity = PatientEntity.class)
public class PatientFtsEntity
{
    @ColumnInfo(name = "name")
    String name;

    @Ignore
    public PatientFtsEntity(){}
    public PatientFtsEntity(String name)
    {
        this.name = name;
    }
}
