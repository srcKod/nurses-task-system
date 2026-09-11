package com.example.enursejobs.db.EntityLocal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enursejobs.api.EntityRemote.AccessTokenEntityRemote;
import com.example.enursejobs.db.model.AccessToken;

import org.checkerframework.checker.nullness.qual.NonNull;

@Entity(tableName = "t_accesstoken")
public class AccessTokenEntity implements AccessToken
{
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "seq")
    int seq;
    @ColumnInfo(name = "access_token")
    String token;
    @ColumnInfo(name = "token_type")
    String tokenType;
    @ColumnInfo(name = "expires_in")
    int expiresIn;
    @ColumnInfo(name = "fcm_token")
    String fcmToken;


    public AccessTokenEntity(){};
    @Ignore
    public AccessTokenEntity(int seq, String token, String tokenType, int expiresIn, String fcmToken)
    {
        this.seq=0;
        this.token=token;
        this.tokenType=tokenType;
        this.expiresIn=expiresIn;
        this.fcmToken=fcmToken;
    };

    @Override
    public int getId() {return this.id;}
    @Override
    public void setId(int id) {this.id=id;}
    @Override
    public int getSeq() {return this.seq;}
    @Override
    public void setSeq(int seq) {this.seq=seq;}
    @Override
    public String getToken() {return this.token;}
    @Override
    public void setToken(String accessToken) {this.token=token;}
    @Override
    public String getTokenType(){return this.tokenType;}
    @Override
    public void setTokenType(String tokenType) {this.tokenType=tokenType;}
    @Override
    public int getExpiresIn() {return this.expiresIn;}
    @Override
    public void setExpiresIn(int expiresIn) {this.expiresIn=expiresIn;}
    @Override
    public String getFcmToken() {return this.fcmToken;}
    @Override
    public void setFcmToken(String fcmToken) {this.fcmToken=fcmToken;}
}
