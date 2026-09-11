package com.example.enursejobs.db.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.example.enursejobs.db.EntityLocal.AccessTokenEntity;

import java.util.List;

@Dao
public interface AccessTokenDao
{
    @Query("SELECT access_token FROM t_accesstoken WHERE seq=0")
    ListenableFuture<String> selectToken();
    @Query("UPDATE t_accesstoken SET access_token=:token WHERE seq=0")
    ListenableFuture<Integer> updateToken(String token);
    @Query("SELECT fcm_token FROM t_accesstoken WHERE seq=0")
    ListenableFuture<String> selectFcmToken();
    @Query("UPDATE t_accesstoken SET fcm_token=:fcmToken WHERE seq=0")
    ListenableFuture<Integer> updateFcmToken(String fcmToken);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertAccessToken(AccessTokenEntity accessToken);
    @Query("DELETE FROM t_accesstoken")
    ListenableFuture<Integer> deleteAccessTokens();

}
