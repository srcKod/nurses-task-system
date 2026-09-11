package com.example.enursejobs.api.ServiceRetrofit;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CaringtypeService
{
    @GET("caringtypes")
    Call<List<CaringtypeEntityRemote>> fetchCaringtypesRemote();
    @POST("caringtypes")
    Call<CaringtypeEntityRemote> createCaringtypeRemote(@Body CaringtypeEntityRemote caringtype);
    @PATCH("caringtypes")
    Call<CaringtypeEntityRemote> updateCaringtypeRemote(@Body CaringtypeEntityRemote caringtype);
    @GET("caringtypes/{id}")
    Call<CaringtypeEntityRemote> fetchCaringtypeRemote(@Path("id") int id);
    @DELETE("caringtypes/{id}")
    Call<CaringtypeEntityRemote> deleteCaringtypeRemote(@Path("id") int id);
}
