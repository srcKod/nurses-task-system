package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;

@Entity(tableName = "caringtype_Fts")
@Fts4(contentEntity = CaringtypeEntity.class)
public class CaringtypeFtsEntity
{
    @ColumnInfo(name= "name")
    String name;
    @ColumnInfo(name= "description")
    String description;

    @Ignore
    public CaringtypeFtsEntity(){}
    public CaringtypeFtsEntity(String name, String description)
    {
        this.name= name;
        this.description = description;
    }
}
