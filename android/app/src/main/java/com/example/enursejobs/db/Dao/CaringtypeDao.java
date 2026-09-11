package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;

import java.util.List;

@Dao
public interface CaringtypeDao
{
    @Query("SELECT * FROM t_caringtype")
    LiveData<List<CaringtypeEntity>> selectCaringtypes();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertCaringtype(CaringtypeEntity caringtype);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertCaringtypes(List<CaringtypeEntity> caringtype);
    @Update
    ListenableFuture<Integer> updateCaringtype(CaringtypeEntity caringtype);
    @Query("select * from t_caringtype where id = :Id")
    LiveData<CaringtypeEntity> selectCaringtype(int Id);
    @Query("DELETE FROM t_caringtype WHERE id = :id")
    ListenableFuture<Integer> deleteCaringtype(int id);
    @Query("SELECT t_caringtype.* FROM t_caringtype JOIN caringtype_Fts ON (t_caringtype.id = caringtype_Fts.rowid) "
            + "WHERE caringtype_Fts MATCH :query")
    LiveData<List<CaringtypeEntity>> SearchCaringtypes(String query);
}
