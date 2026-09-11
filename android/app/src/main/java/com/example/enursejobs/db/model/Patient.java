package com.example.enursejobs.db.model;

public interface Patient
{
    int getId();
    void setId(int id);
    String getName();
    void setName(String name);
    String getRoomPhotoPath();
    void setRoomPhotoPath(String roomPhotoPath);
    int getIsStopped();
    void setIsStopped(int is_stopped);
}
