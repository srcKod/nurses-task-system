package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.PatientEntity;

import java.util.List;

@Dao
public interface PatientDao
{
    @Query("SELECT * FROM t_patient")
    LiveData<List<PatientEntity>> selectPatients();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertPatient(PatientEntity patient);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertPatients(List<PatientEntity> patient);
    @Update
    ListenableFuture<Integer> updatePatient(PatientEntity patient);
    @Query("select * from t_patient where id = :Id")
    LiveData<PatientEntity> selectPatient(int Id);
    @Query("DELETE FROM t_patient WHERE id = :id")
    ListenableFuture<Integer> deletePatient(int id);
    @Query("SELECT t_patient.* FROM t_patient JOIN patient_Fts ON (t_patient.id = patient_Fts.rowid) "
            + "WHERE patient_Fts MATCH :query")
    LiveData<List<PatientEntity>> SearchPatients(String query);
}
