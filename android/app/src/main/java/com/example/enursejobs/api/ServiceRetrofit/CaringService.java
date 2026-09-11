package com.example.enursejobs.api.ServiceRetrofit;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.CaringEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CaringService
{
    @GET("carings")
    Call<List<CaringEntityRemote>> fetchCaringsRemote();
    @POST("carings")
    Call<CaringEntityRemote> createCaringRemote(@Body CaringEntityRemote caring);
    @GET("carings/trashed")
    Call<List<CaringEntityRemote>> fetchTrashedCaringsRemote();
    @PATCH("carings/trashed")
    Call<CaringEntityRemote> updateTrashedCaringRemote(@Body CaringEntityRemote caring);
    @GET("carings/trashed/{id}")
    Call<CaringEntityRemote> fetchTrashedCaringRemote(@Path("id") int id);
    @DELETE("carings/prune/{id}")
    Call<CaringtypeEntityRemote> pruneCaringRemote(@Path("id") int id);
    @GET("carings/{id}")
    Call<CaringEntityRemote> fetchCaringRemote(@Path("id") int id);
    @DELETE("carings/{id}")
    Call<CaringtypeEntityRemote> deleteCaringRemote(@Path("id") int id);
    @PATCH("carings")
    Call<CaringEntityRemote> updateCaringRemote(@Body CaringEntityRemote caring);
    @GET("carings/nurse/{nurse_id}")
    Call<List<CaringEntityRemote>> fetchCaringsByNurseIdRemote(@Path("nurse_id") int nurseId);
    @GET("carings/caringtype/{caringtype_id}")
    Call<List<CaringEntityRemote>> fetchCaringsByCaringtypeIdRemote(@Path("nurse_id") int caringtypeId);
    @GET("carings/patient/{patient_id}")
    Call<List<CaringEntityRemote>> fetchCaringsByPatientIdRemote(@Path("patient_id") int patientId);
    @GET("carings/check/{id}")
    Call<CaringEntityRemote> checkCaringRemote(@Path("id") int id);
}
