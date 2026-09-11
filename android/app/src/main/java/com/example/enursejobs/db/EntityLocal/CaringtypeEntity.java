package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;
import com.example.enursejobs.db.model.Caringtype;

import org.checkerframework.checker.nullness.qual.NonNull;

@Entity(tableName = "t_caringtype")
public class CaringtypeEntity implements Caringtype
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name= "id")
    int id;
    @ColumnInfo(name= "name")
    String name;
    @ColumnInfo(name= "description")
    String description;

    @Ignore
    public CaringtypeEntity(){}
    public CaringtypeEntity(int id, String name, String description)
    {
        this.id = id;
        this.name= name;
        this.description = description;
    }

    public CaringtypeEntity(@NonNull CaringtypeEntity caringtypeEntity)
    {
        this.id = caringtypeEntity.id;
        this.name= caringtypeEntity.name;
        this.description = caringtypeEntity.description;
    }

    public CaringtypeEntity(@NonNull CaringtypeEntityRemote caringtypeEntityRemote)
    {
        this.id = caringtypeEntityRemote.getId();
        this.name= caringtypeEntityRemote.getName();
        this.description = caringtypeEntityRemote.getDescription();
    }

    @Override
    public int getId(){return this.id;}
    @Override
    public void setId(int id){this.id = id;}
    @Override
    public String getName(){return this.name;}
    @Override
    public void setName(String name){this.name = name;}
    @Override
    public String getDescription(){return this.description;}
    @Override
    public void setDescription(String description){this.description = description;}
}
