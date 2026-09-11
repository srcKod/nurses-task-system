package com.example.enursejobs.api.EntityRemote;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CaringtypeEntityRemote
{
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;

    public CaringtypeEntityRemote(){}
    public CaringtypeEntityRemote(int id, String name, String description)
    {
        this.id = id;
        this.name= name;
        this.description = description;
    }
    public CaringtypeEntityRemote(@NonNull CaringtypeEntityRemote caringtypeEntityRemote)
    {
        this.id = caringtypeEntityRemote.id;
        this.name= caringtypeEntityRemote.name;
        this.description = caringtypeEntityRemote.description;
    }
    public CaringtypeEntityRemote(@NonNull CaringtypeEntity caringtypeEntity)
    {
        this.id = caringtypeEntity.getId();
        this.name= caringtypeEntity.getName();
        this.description = caringtypeEntity.getDescription();
    }

    public int getId(){return this.id;}
    public void setId(int id){this.id = id;}
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    public String getDescription(){return this.description;}
    public void setDescription(String description){this.description = description;}
}
