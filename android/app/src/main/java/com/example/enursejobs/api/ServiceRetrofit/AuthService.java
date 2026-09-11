package com.example.enursejobs.api.ServiceRetrofit;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.AccessTokenEntityRemote;
import com.example.enursejobs.api.EntityRemote.AuthEntityRemote;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService
{
    @POST("auth/login")
    Call<AccessTokenEntityRemote> fetchAccessTokenRemote(@Body AuthEntityRemote auth);
    @POST("auth/register")
    Call<JsonObject> createAccessTokenRemote(@Body AuthEntityRemote auth);
    @POST("auth/refresh")
    Call<AccessTokenEntityRemote> refreshAccessTokenRemote(@Body String accessToken);
    @GET("auth/nurseProfile")
    Call<NurseEntityRemote> fetchProfile();
}
