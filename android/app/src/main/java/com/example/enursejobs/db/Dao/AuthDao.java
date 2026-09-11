package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.AccessTokenEntity;
import com.example.enursejobs.db.EntityLocal.AuthEntity;

@Dao
public interface AuthDao
{
    @Query("SELECT * FROM t_auth WHERE seq=0")
    LiveData<AuthEntity> selectAuth();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertAuth(AuthEntity auth);
    @Query("UPDATE t_auth SET id=:id, name=:name, email=:email WHERE seq=0")
    ListenableFuture<Integer> updateAuth(int id, String name, String email);
    @Query("UPDATE t_auth SET email=:email WHERE seq=0")
    ListenableFuture<Integer> updateAuthEmail(String email);
    @Query("UPDATE t_auth SET password=:password WHERE seq=0")
    ListenableFuture<Integer> updateAuthPassword(String password);
    @Query("DELETE FROM t_auth")
    ListenableFuture<Integer> deleteAuths();
}
