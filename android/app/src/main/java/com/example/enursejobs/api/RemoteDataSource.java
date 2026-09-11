package com.example.enursejobs.api;

import com.example.enursejobs.BuildConfig;
import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.AccessTokenEntityRemote;
import com.example.enursejobs.api.EntityRemote.AuthEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;
import com.example.enursejobs.api.EntityRemote.PatientEntityRemote;

import java.util.List;

import retrofit2.Response;

public interface RemoteDataSource
{
    // Base URL per build type, injected from app/build.gradle:
    //   debug   -> http://10.0.2.2:8000/api/            (emulator host machine)
    //   release -> https://nurses-task-system.wasmer.app/api/ (HTTPS staging)
    // Override per machine with apiUrlDebug / apiUrlRelease in gradle.properties
    // (e.g. http://192.168.1.50:8000/api/ for a physical device on the LAN).
    String API_URL = BuildConfig.API_URL;

    /** Auth **/
    void fetchAccessTokenRemote(AuthEntityRemote auth, CallbackListener<AccessTokenEntityRemote> listener);
    void createAccessTokenRemote(AuthEntityRemote nurse, CallbackListener<JsonObject> listener);
    void refreshAccessTokenRemote(String accessToken, CallbackListener<AccessTokenEntityRemote> listener);
    void fetchProfileRemote(CallbackListener<NurseEntityRemote> listener);

    /** Nurse **/
    void fetchNursesRemote(CallbackListener<List<NurseEntityRemote>> listener);
    void createNurseRemote(NurseEntityRemote nurse, CallbackListener<NurseEntityRemote> listener);
    void updateNurseRemote(NurseEntityRemote nurse, CallbackListener<NurseEntityRemote> listener);
    void fetchNurseRemote(int id, CallbackListener<NurseEntityRemote> listener);
    void deleteNurseRemote(int id, CallbackListener<NurseEntityRemote> listener);

    /** Caringtype **/
    void fetchCaringtypeRemote(int id, CallbackListener<CaringtypeEntityRemote> listener);
    void createCaringtypeRemote(CaringtypeEntityRemote caringtype, CallbackListener<CaringtypeEntityRemote> listener);
    void updateCaringtypeRemote(CaringtypeEntityRemote caringtype, CallbackListener<CaringtypeEntityRemote> listener);
    void fetchCaringtypesRemote(CallbackListener<List<CaringtypeEntityRemote>> listener);
    void deleteCaringtypeRemote(int id, CallbackListener<CaringtypeEntityRemote> listener);

    /** Patient **/
    void fetchPatientsRemote(CallbackListener<List<PatientEntityRemote>> listener);
    void createPatientRemote(PatientEntityRemote patient, CallbackListener<PatientEntityRemote> listener);
    void updatePatientRemote(PatientEntityRemote patient, CallbackListener<PatientEntityRemote> listener);
    void fetchPatientRemote(int id, CallbackListener<PatientEntityRemote> listener);
    void deletePatientRemote(int id, CallbackListener<PatientEntityRemote> listener);

    /** Caring **/
    void fetchCaringsRemote(CallbackListener<List<CaringEntityRemote>> listener);
    void createCaringRemote(CaringEntityRemote caring, CallbackListener<CaringEntityRemote> listener);
    void fetchTrashedCaringsRemote(CallbackListener<List<CaringEntityRemote>> listener);
    void updateTrashedCaringRemote(CaringEntityRemote caring, CallbackListener<CaringEntityRemote> listener);
    void fetchTrashedCaringRemote(int id, CallbackListener<CaringEntityRemote> listener);
    void pruneCaringRemote(int id, CallbackListener<CaringtypeEntityRemote> listener);
    void fetchCaringRemote(int id, CallbackListener<CaringEntityRemote> listener);
    void deleteCaringRemote(int id, CallbackListener<CaringtypeEntityRemote> listener);
    void updateCaringRemote(CaringEntityRemote caring, CallbackListener<CaringEntityRemote> listener);
    void fetchCaringsByNurseIdRemote(int nurseId, CallbackListener<List<CaringEntityRemote>> listener);
    void fetchCaringsByCaringtypeIdRemote(int caringtypeId, CallbackListener<List<CaringEntityRemote>> listener);
    void fetchCaringsByPatientIdRemote(int patientId, CallbackListener<List<CaringEntityRemote>> listener);
    void checkCaringRemote(int id, CallbackListener<CaringEntityRemote> listener);
}
