package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.CaringEntity;

import java.util.List;

@Dao
public interface CaringDao {
    @Query("SELECT * FROM t_caring")
    LiveData<List<CaringEntity>> selectCarings();
    @Query("SELECT * FROM t_caring WHERE nurse_id = :nurseId")
    LiveData<List<CaringEntity>> selectCaringsByNurseId(int nurseId);
    @Query("SELECT * FROM t_caring WHERE caringtype_id = :caringtypeId")
    LiveData<List<CaringEntity>> selectCaringsByCaringtypeId(int caringtypeId);
    @Query("SELECT * FROM t_caring WHERE patient_id = :patientId")
    LiveData<List<CaringEntity>> selectCaringsByPatientId(int patientId);
    @Query("SELECT * FROM t_caring WHERE status='Finished'")
    LiveData<List<CaringEntity>> selectTrashedCarings();
    @Query("SELECT * FROM t_caring WHERE status='Finished' AND id=:id")
    LiveData<CaringEntity> selectTrashedCaring(int id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertCaring(CaringEntity caring);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertCarings(List<CaringEntity> caring);
    @Update
    ListenableFuture<Integer> updateCaring(CaringEntity caring);
    @Query("select * from t_caring where id = :Id")
    LiveData<CaringEntity> selectCaring(int Id);
    @Query("DELETE FROM t_caring WHERE id = :id")
    ListenableFuture<Integer> deleteCaring(int id);
    @Query("SELECT t_caring.* FROM t_caring JOIN caring_Fts ON (t_caring.id = caring_Fts.rowid) "
            + "WHERE caring_Fts MATCH :query")
    LiveData<List<CaringEntity>> SearchCarings(String query);
    @Query("SELECT t_caring.* FROM t_caring JOIN caring_Fts ON (t_caring.id = caring_Fts.rowid AND t_caring.nurse_id =:nurseid) "
            + "WHERE caring_Fts MATCH :query")
    LiveData<List<CaringEntity>> SearchCaringsByNurseId(String query, int nurseid);
}
