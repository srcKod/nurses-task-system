package com.example.enursejobs.api.ServiceRetrofit;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.example.enursejobs.api.CallbackListener;
import com.example.enursejobs.api.EntityRemote.AccessTokenEntityRemote;
import com.example.enursejobs.api.EntityRemote.AuthEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;
import com.example.enursejobs.api.EntityRemote.PatientEntityRemote;
import com.example.enursejobs.api.RemoteDataSource;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitDataSource implements RemoteDataSource {

    private static final String TAG = "RetrofitDataSource";
    private static RetrofitDataSource instance = null;

    /** Services Implementation **/
    private final AuthService AuthServiceImpl;
    private final NurseService NurseServiceImpl;
    private final CaringtypeService CaringtypeServiceImpl;
    private final PatientService PatientServiceImpl;
    private final CaringService CaringServiceImpl;

    public RetrofitDataSource(Context context){
        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new Authenticator(context))
                .addInterceptor(new AccessTokenInterceptor(context))
                .addInterceptor(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new  Retrofit.Builder()
                                .baseUrl(API_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build();

        AuthServiceImpl = retrofit.create(AuthService.class);
        NurseServiceImpl = retrofit.create(NurseService.class);
        CaringtypeServiceImpl = retrofit.create(CaringtypeService.class);
        PatientServiceImpl = retrofit.create(PatientService.class);
        CaringServiceImpl = retrofit.create(CaringService.class);
    }
    public static synchronized RetrofitDataSource getInstance(Context context){
        if (null == instance)
            instance = new RetrofitDataSource(context);
        return instance;
    }
    //this is so you don't need to pass context each time
    public static synchronized RetrofitDataSource getInstance(){
        if (null == instance)
        {
            throw new IllegalStateException(RetrofitDataSource.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    /** Auth Api Methods **/
    @Override
    public void fetchAccessTokenRemote(AuthEntityRemote auth, final CallbackListener<AccessTokenEntityRemote> listener) {
        Call<AccessTokenEntityRemote> callAuthService = AuthServiceImpl.fetchAccessTokenRemote(auth);
        callAuthService.enqueue(new retrofit2.Callback<AccessTokenEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<AccessTokenEntityRemote> call,
                                   @NonNull Response<AccessTokenEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessTokenEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void createAccessTokenRemote(AuthEntityRemote auth, final CallbackListener<JsonObject> listener) {
        Call<JsonObject> callAuthService = AuthServiceImpl.createAccessTokenRemote(auth);
        callAuthService.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call,
                                   @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void refreshAccessTokenRemote(String accessToken, final CallbackListener<AccessTokenEntityRemote> listener) {
        Call<AccessTokenEntityRemote> callAuthService = AuthServiceImpl.refreshAccessTokenRemote(accessToken);
        callAuthService.enqueue(new retrofit2.Callback<AccessTokenEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<AccessTokenEntityRemote> call,
                                   @NonNull Response<AccessTokenEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessTokenEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchProfileRemote(final CallbackListener<NurseEntityRemote> listener) {
        Call<NurseEntityRemote> callAuthService = AuthServiceImpl.fetchProfile();
        callAuthService.enqueue(new retrofit2.Callback<NurseEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<NurseEntityRemote> call,
                                   @NonNull Response<NurseEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NurseEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    /** Nurse Api Methods **/
    @Override
    public void fetchNursesRemote(final CallbackListener<List<NurseEntityRemote>> listener) {
        Call<List<NurseEntityRemote>> callNurseService = NurseServiceImpl.fetchNursesRemote();
        callNurseService.enqueue(new retrofit2.Callback<List<NurseEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<NurseEntityRemote>> call,
                                   @NonNull Response<List<NurseEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NurseEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchNurseRemote(int id, final CallbackListener<NurseEntityRemote> listener) {
        Call<NurseEntityRemote> callNurseService = NurseServiceImpl.fetchNurseRemote(id);
        callNurseService.enqueue(new retrofit2.Callback<NurseEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<NurseEntityRemote> call,
                                   @NonNull Response<NurseEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NurseEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void createNurseRemote(NurseEntityRemote nurse, final CallbackListener<NurseEntityRemote> listener) {
        Call<NurseEntityRemote> callNurseService = NurseServiceImpl.createNurseRemote(nurse);
        callNurseService.enqueue(new retrofit2.Callback<NurseEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<NurseEntityRemote> call,
                                   @NonNull Response<NurseEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NurseEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void updateNurseRemote(NurseEntityRemote nurse, final CallbackListener<NurseEntityRemote> listener) {
        Call<NurseEntityRemote> callNurseService = NurseServiceImpl.updateNurseRemote(nurse);
        callNurseService.enqueue(new retrofit2.Callback<NurseEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<NurseEntityRemote> call,
                                   @NonNull Response<NurseEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NurseEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    public void updateNurseFcmRemote(NurseEntityRemote nurse, final CallbackListener<JsonObject> listener){
        Call<JsonObject> callNurseService = NurseServiceImpl.updateNurseFcmRemote(nurse);
        callNurseService.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call,
                                   @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        assert response.body() != null;
                        listener.getResult(response.body().getAsJsonObject());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void deleteNurseRemote(int id, final CallbackListener<NurseEntityRemote> listener) {
        Call<NurseEntityRemote> callNurseService = NurseServiceImpl.deleteNurseRemote(id);
        callNurseService.enqueue(new retrofit2.Callback<NurseEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<NurseEntityRemote> call,
                                   @NonNull Response<NurseEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        assert response.body() != null;
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NurseEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }



    /** Caringtype Api Methods **/
    @Override
    public void fetchCaringtypesRemote(final CallbackListener<List<CaringtypeEntityRemote>> listener) {
        Call<List<CaringtypeEntityRemote>> callCaringtypeService = CaringtypeServiceImpl.fetchCaringtypesRemote();
        callCaringtypeService.enqueue(new retrofit2.Callback<List<CaringtypeEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringtypeEntityRemote>> call,
                                   @NonNull Response<List<CaringtypeEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringtypeEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void createCaringtypeRemote(CaringtypeEntityRemote caringtype, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringtypeService = CaringtypeServiceImpl.createCaringtypeRemote(caringtype);
        callCaringtypeService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void updateCaringtypeRemote(CaringtypeEntityRemote caringtype, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringtypeService = CaringtypeServiceImpl.updateCaringtypeRemote(caringtype);
        callCaringtypeService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchCaringtypeRemote(int id, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringtypeService = CaringtypeServiceImpl.fetchCaringtypeRemote(id);
        callCaringtypeService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void deleteCaringtypeRemote(int id, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringtypeService = CaringtypeServiceImpl.deleteCaringtypeRemote(id);
        callCaringtypeService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    /** Patient Api Methods **/
    @Override
    public void fetchPatientsRemote(final CallbackListener<List<PatientEntityRemote>> listener) {
        Call<List<PatientEntityRemote>> callPatientService = PatientServiceImpl.fetchPatientsRemote();
        callPatientService.enqueue(new retrofit2.Callback<List<PatientEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<PatientEntityRemote>> call,
                                   @NonNull Response<List<PatientEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PatientEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void createPatientRemote(PatientEntityRemote patient, final CallbackListener<PatientEntityRemote> listener) {
        Call<PatientEntityRemote> callPatientService = PatientServiceImpl.createPatientRemote(patient);
        callPatientService.enqueue(new retrofit2.Callback<PatientEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<PatientEntityRemote> call,
                                   @NonNull Response<PatientEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void updatePatientRemote(PatientEntityRemote patient, final CallbackListener<PatientEntityRemote> listener) {
        Call<PatientEntityRemote> callPatientService = PatientServiceImpl.updatePatientRemote(patient);
        callPatientService.enqueue(new retrofit2.Callback<PatientEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<PatientEntityRemote> call,
                                   @NonNull Response<PatientEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchPatientRemote(int id, final CallbackListener<PatientEntityRemote> listener) {
        Call<PatientEntityRemote> callPatientService = PatientServiceImpl.fetchPatientRemote(id);
        callPatientService.enqueue(new retrofit2.Callback<PatientEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<PatientEntityRemote> call,
                                   @NonNull Response<PatientEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void deletePatientRemote(int id, final CallbackListener<PatientEntityRemote> listener) {
        Call<PatientEntityRemote> callPatientService = PatientServiceImpl.deletePatientRemote(id);
        callPatientService.enqueue(new retrofit2.Callback<PatientEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<PatientEntityRemote> call,
                                   @NonNull Response<PatientEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    /** Caring Api Methods **/
    @Override
    public void fetchCaringsRemote(final CallbackListener<List<CaringEntityRemote>> listener) {
        Call<List<CaringEntityRemote>> callCaringService = CaringServiceImpl.fetchCaringsRemote();
        callCaringService.enqueue(new retrofit2.Callback<List<CaringEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringEntityRemote>> call,
                                   @NonNull Response<List<CaringEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void createCaringRemote(CaringEntityRemote caring, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.createCaringRemote(caring);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchTrashedCaringsRemote(final CallbackListener<List<CaringEntityRemote>> listener) {
        Call<List<CaringEntityRemote>> callCaringService = CaringServiceImpl.fetchTrashedCaringsRemote();
        callCaringService.enqueue(new retrofit2.Callback<List<CaringEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringEntityRemote>> call,
                                   @NonNull Response<List<CaringEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void updateTrashedCaringRemote(CaringEntityRemote caring, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.updateTrashedCaringRemote(caring);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchTrashedCaringRemote(int id, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.fetchTrashedCaringRemote(id);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void pruneCaringRemote(int id, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringService = CaringServiceImpl.pruneCaringRemote(id);
        callCaringService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchCaringRemote(int id, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.fetchCaringRemote(id);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void deleteCaringRemote(int id, final CallbackListener<CaringtypeEntityRemote> listener) {
        Call<CaringtypeEntityRemote> callCaringService = CaringServiceImpl.deleteCaringRemote(id);
        callCaringService.enqueue(new retrofit2.Callback<CaringtypeEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringtypeEntityRemote> call,
                                   @NonNull Response<CaringtypeEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringtypeEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void updateCaringRemote(CaringEntityRemote caring, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.updateCaringRemote(caring);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchCaringsByNurseIdRemote(int nurseId, final CallbackListener<List<CaringEntityRemote>> listener) {
        Call<List<CaringEntityRemote>> callCaringService = CaringServiceImpl.fetchCaringsByNurseIdRemote(nurseId);
        callCaringService.enqueue(new retrofit2.Callback<List<CaringEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringEntityRemote>> call,
                                   @NonNull Response<List<CaringEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchCaringsByCaringtypeIdRemote(int caringtypeId, final CallbackListener<List<CaringEntityRemote>> listener) {
        Call<List<CaringEntityRemote>> callCaringService = CaringServiceImpl.fetchCaringsByCaringtypeIdRemote(caringtypeId);
        callCaringService.enqueue(new retrofit2.Callback<List<CaringEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringEntityRemote>> call,
                                   @NonNull Response<List<CaringEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void fetchCaringsByPatientIdRemote(int patientId, final CallbackListener<List<CaringEntityRemote>> listener) {
        Call<List<CaringEntityRemote>> callCaringService = CaringServiceImpl.fetchCaringsByPatientIdRemote(patientId);
        callCaringService.enqueue(new retrofit2.Callback<List<CaringEntityRemote>>() {
            @Override
            public void onResponse(@NonNull Call<List<CaringEntityRemote>> call,
                                   @NonNull Response<List<CaringEntityRemote>> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CaringEntityRemote>> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void checkCaringRemote(int id, final CallbackListener<CaringEntityRemote> listener) {
        Call<CaringEntityRemote> callCaringService = CaringServiceImpl.checkCaringRemote(id);
        callCaringService.enqueue(new retrofit2.Callback<CaringEntityRemote>() {
            @Override
            public void onResponse(@NonNull Call<CaringEntityRemote> call,
                                   @NonNull Response<CaringEntityRemote> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, response.toString());
                        listener.getResult(response.body());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaringEntityRemote> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
