package com.example.enursejobs.api.ServiceRetrofit;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.PatientEntityRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PatientService
{
    @GET("patients")
    Call<List<PatientEntityRemote>> fetchPatientsRemote();
    @POST("patients")
    Call<PatientEntityRemote> createPatientRemote(@Body PatientEntityRemote patient);
    @PATCH("patients")
    Call<PatientEntityRemote> updatePatientRemote(@Body PatientEntityRemote patient);
    @GET("patients/{id}")
    Call<PatientEntityRemote> fetchPatientRemote(@Path("id") int id);
    @DELETE("patients/{id}")
    Call<PatientEntityRemote> deletePatientRemote(@Path("id") int id);

}
