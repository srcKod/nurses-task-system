package com.example.enursejobs.db.model;

public interface AccessToken
{
    int getId();
    void setId(int id);
    int getSeq();
    void setSeq(int id);
    String getToken();
    void setToken(String accessToken);
    String getTokenType();
    void setTokenType(String tokenType);
    int getExpiresIn();
    void setExpiresIn(int expiresIn);
    String getFcmToken();
    void setFcmToken(String fcmToken);
}
