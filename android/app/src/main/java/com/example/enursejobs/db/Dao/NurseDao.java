package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

import java.util.List;

@Dao
public interface NurseDao
{
    // Returns the number of users inserted.
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    ListenableFuture<Integer> insertNurses(List<NurseEntity> nurses);

    @Query("SELECT * FROM t_nurse")
    LiveData<List<NurseEntity>> selectNurses();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertNurse(NurseEntity nurse);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertNurses(List<NurseEntity> nurse);
    @Update
    ListenableFuture<Integer> updateNurse(NurseEntity nurse);
    @Query("DELETE FROM t_nurse WHERE id = :id")
    ListenableFuture<Integer> deleteNurse(int id);
    @Query("select * from t_nurse where id = :id")
    LiveData<NurseEntity> selectNurse(int id);
    @Query("select * from t_nurse where email = :email")
    ListenableFuture<NurseEntity> findNurse(String email);
    @Query("SELECT t_nurse.* FROM t_nurse JOIN nurse_Fts ON (t_nurse.id = nurse_Fts.rowid) "
            + "WHERE nurse_Fts MATCH :query")
    LiveData<List<NurseEntity>> SearchNurses(String query);
    @Query("SELECT fcm_token FROM t_nurse WHERE email= :email")
    LiveData<String> selectFcmToken(String email);
    @Query("UPDATE t_nurse SET fcm_token= :fcm_token WHERE email= :email")
    ListenableFuture<Integer> updateFcmToken(String email, String fcm_token);
}
