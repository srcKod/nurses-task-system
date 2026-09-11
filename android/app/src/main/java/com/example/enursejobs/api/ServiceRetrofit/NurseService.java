package com.example.enursejobs.api.ServiceRetrofit;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NurseService
{
    @GET("nurses")
    Call<List<NurseEntityRemote>> fetchNursesRemote();
    @POST("nurses")
    Call<NurseEntityRemote> createNurseRemote(@Body NurseEntityRemote nurse);
    @PATCH("nurses")
    Call<NurseEntityRemote> updateNurseRemote(@Body NurseEntityRemote nurse);
    @PATCH("nurses/fcmtoken")
    Call<JsonObject> updateNurseFcmRemote(@Body NurseEntityRemote nurse);
    @GET("nurses/{id}")
    Call<NurseEntityRemote> fetchNurseRemote(@Path("id") int id);
    @DELETE("nurses/{id}")
    Call<NurseEntityRemote> deleteNurseRemote(@Path("id") int id);

}
