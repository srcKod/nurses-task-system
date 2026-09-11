package com.example.enursejobs.api.EntityRemote;

import com.google.gson.annotations.SerializedName;
import com.example.enursejobs.db.EntityLocal.AccessTokenEntity;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AccessTokenEntityRemote
{
    @SerializedName("access_token")
    String token;
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("expires_in")
    int expiresIn;

    public AccessTokenEntityRemote(){}
    public AccessTokenEntityRemote(String accessToken, String tokenType, int expiresIn)
    {
        this.token=accessToken;
        this.tokenType=tokenType;
        this.expiresIn=expiresIn;
    }
    public AccessTokenEntityRemote(@NonNull AccessTokenEntityRemote accessToken)
    {
        this.token=accessToken.token;
        this.tokenType=accessToken.tokenType;
        this.expiresIn=accessToken.expiresIn;
    }

    public AccessTokenEntityRemote(@NonNull AccessTokenEntity accessToken)
    {
        this.token=accessToken.getToken();
        this.tokenType=accessToken.getTokenType();
        this.expiresIn=accessToken.getExpiresIn();
    }

    public String getAccessToken() {
        return token;
    }
    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }
    public String getTokenType() {
        return this.tokenType;
    }
    public void setTokenType(String tokenType){
        this.tokenType = tokenType;
    }
    public int getExpireIn() {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn){
        this.expiresIn = expiresIn;
    }
}
