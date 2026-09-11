package com.example.enursejobs.db.EntityLocal;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;

@Entity(tableName = "nurse_Fts")
@Fts4(contentEntity = NurseEntity.class)
public class NurseFtsEntity
{
    @ColumnInfo(name= "name")
    String name;
    @ColumnInfo(name= "email")
    String email;
    @ColumnInfo(name= "phone")
    String phone;

    @Ignore
    public NurseFtsEntity(){}
    public NurseFtsEntity(String name, String email, String phone)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
