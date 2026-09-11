package com.example.enursejobs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonObject;
import com.example.enursejobs.api.EntityRemote.AuthEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringEntityRemote;
import com.example.enursejobs.api.EntityRemote.CaringtypeEntityRemote;
import com.example.enursejobs.api.EntityRemote.NurseEntityRemote;
import com.example.enursejobs.api.EntityRemote.PatientEntityRemote;
import com.example.enursejobs.api.RemoteDataSource;
import com.example.enursejobs.api.ServiceRetrofit.RetrofitDataSource;
import com.example.enursejobs.db.AppDatabase;
import com.example.enursejobs.db.EntityLocal.AuthEntity;
import com.example.enursejobs.db.EntityLocal.CaringEntity;
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;
import com.example.enursejobs.db.EntityLocal.NurseEntity;
import com.example.enursejobs.db.EntityLocal.PatientEntity;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataRepository
{
    private static final String TAG = "DataRepository";
    private static volatile DataRepository sInstance;

    private final AppDatabase mLocalDatabase;
    private final RemoteDataSource mRemoteDataSource;

    private DataRepository(final AppDatabase localDatabase, final RemoteDataSource remoteDataSource) {
        this.mLocalDatabase = localDatabase;
        this.mRemoteDataSource = remoteDataSource ;
    }

    public static DataRepository getInstance(final AppDatabase localDatabase,
                                             final RemoteDataSource remote) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(localDatabase, remote);
                }
            }
        }
        return sInstance;
    }

    /** AccessToken Repository Methods **/
    public LiveData<String> login(AuthEntity auth) {
        MutableLiveData<String> AccessTokenObservable = new MutableLiveData<>();
        mRemoteDataSource.fetchAccessTokenRemote(new AuthEntityRemote(auth),
                accessTokenEntityRemote -> {
                    if (accessTokenEntityRemote != null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.AccessTokenDao()
                                           .updateToken(accessTokenEntityRemote.getAccessToken()),
                                   new FutureCallback<Integer>() {
                                       @Override
                                       public void onSuccess(Integer result) {
                                           Log.d(TAG, "Access Token Updated: "+accessTokenEntityRemote.getAccessToken());
                                           AccessTokenObservable.postValue(accessTokenEntityRemote.getAccessToken());
                                       }

                                       @Override
                                       public void onFailure(@NonNull Throwable t) {
                                           Log.d(TAG, t.getMessage());
                                       }
                                   }, new AppExecutors().diskIO());

//                            Log.d(TAG,"Access Token updated successfully");
//                            AccessTokenObservable.postValue(accessTokenEntityRemote.getAccessToken());

                            Futures.addCallback(mLocalDatabase.AccessTokenDao().selectFcmToken(),
                                    new FutureCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            NurseEntityRemote nurse = new NurseEntityRemote();
                                            nurse.setName(auth.getName());
                                            nurse.setEmail(auth.getEmail());
                                            nurse.setPassword(null);
                                            nurse.setFcmToken(result);
                                            mRemoteDataSource.updateNurseRemote(nurse,
                                                    nurseEntityRemote->{
                                                        if(nurseEntityRemote != null){
                                                            Log.d(TAG, "fcm token updated remotely for user " +
                                                                    auth.getEmail());
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG, t.getMessage());
                                        }
                                    }, new AppExecutors().diskIO());

    //                                    String fcmToken = mLocalDatabase.AccessTokenDao().selectFcmToken();
                        }
                    }
                });
        return AccessTokenObservable;
    }
    public LiveData<String> getDbAccessToken(){
        MutableLiveData<String> AccessTokenObservable = new MutableLiveData<>();
        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
            Futures.addCallback(mLocalDatabase.AccessTokenDao().selectToken(),
                    new FutureCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    AccessTokenObservable.postValue(result);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.d(TAG,"Couldn't select access token from database");
                }
            }, new AppExecutors().diskIO());
        }
        return AccessTokenObservable;
    }
    public LiveData<String> refreshAccessToken(String token) {
        MutableLiveData<String> AccessTokenObservable = new MutableLiveData<>();
        mRemoteDataSource.refreshAccessTokenRemote(token,
                accessTokenEntityRemote -> {
                    if (accessTokenEntityRemote != null) {
                        if (mLocalDatabase.getDatabaseCreated().getValue() != null) {
                            new AppExecutors().diskIO().execute(()-> {
                                mLocalDatabase.AccessTokenDao()
                                        .updateToken(accessTokenEntityRemote.getAccessToken());
                                Log.d(TAG,"Access Token updated successfully");
                                AccessTokenObservable.postValue(accessTokenEntityRemote.getAccessToken());
                            });
                        }
                    }
                });
        return AccessTokenObservable;
    }
    public LiveData<JsonObject> addAccessToken(AuthEntity auth) {
        MutableLiveData<JsonObject> AccessTokenObservable = new MutableLiveData<>();
        mRemoteDataSource.createAccessTokenRemote(new AuthEntityRemote(auth),
                jsonobj -> {if (jsonobj != null) { AccessTokenObservable.postValue(jsonobj); }});
        return AccessTokenObservable;
    }
    public LiveData<AuthEntity> getProfile() {
        MutableLiveData<AuthEntity> AuthObservable = new MutableLiveData<>();
        mRemoteDataSource.fetchProfileRemote(
                nurseEntityRemote -> {
                    if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                        Futures.addCallback(mLocalDatabase.AuthDao()
                                        .updateAuth(nurseEntityRemote.getId(), nurseEntityRemote.getName(), nurseEntityRemote.getEmail()),
                                new FutureCallback<Integer>() {
                                    @Override
                                    public void onSuccess(Integer result) {
                                        Log.d(TAG,"Auth updated successfully");
                                        AuthObservable.postValue(new AuthEntity(nurseEntityRemote));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Throwable t) {
                                        Log.d(TAG, Objects.requireNonNull(t.getMessage()));
                                    }
                                }, new AppExecutors().diskIO());
                    }
                });
        return AuthObservable;
    }

    /** Nurse Repository Methods **/
    public LiveData<List<NurseEntity>> getNurses(){
        mRemoteDataSource.fetchNursesRemote(
                nurses -> {
                    if (!nurses.isEmpty()) {
                        ArrayList<NurseEntity> _nurses = new ArrayList<>();
                        nurses.forEach(nurse -> _nurses.add(new NurseEntity(nurse)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.NurseDao().insertNurses(_nurses),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Nurses Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Nurses insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.NurseDao().selectNurses();
    }
    public LiveData<NurseEntity> getNurse(int id){
        mRemoteDataSource.fetchNurseRemote(id,
                nurse -> {
                    if (nurse !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.NurseDao().insertNurse(new NurseEntity(nurse)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Nurses Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Nurses insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.NurseDao().selectNurse(id);
    }
    public LiveData<Long> addNurse(NurseEntity nurse){
        MutableLiveData<Long> nonce = new MutableLiveData<>();
        mRemoteDataSource.createNurseRemote(new NurseEntityRemote(nurse),
                remoteNurse -> {
                    if (remoteNurse !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.NurseDao().insertNurse(new NurseEntity(remoteNurse)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Nurses Inserted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Nurses insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> editNurse(NurseEntity nurse){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.updateNurseRemote(new NurseEntityRemote(nurse),
                remoteNurse -> {
                    if (remoteNurse !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.NurseDao().updateNurse(new NurseEntity(remoteNurse)),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Nurses Updated successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Nurses Update failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Boolean> deleteNurse(int id){
        MutableLiveData<Boolean> nonce = new MutableLiveData<>();
        mRemoteDataSource.deleteNurseRemote(id,
                nurseEntityRemote -> {
                    if (nurseEntityRemote != null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.NurseDao().deleteNurse(id),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Nurse Deleted successfully");
                                            nonce.postValue(true);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Nurse Delete failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        Log.d(TAG,"nonce is: "+nonce.getValue());
        return nonce;
    }
    public LiveData<List<NurseEntity>> findNurses(String query) {
        return mLocalDatabase.NurseDao().SearchNurses(query);
    }

    /** Caringtype Repository Methods **/
    public LiveData<List<CaringtypeEntity>> getCaringtypes() {
        mRemoteDataSource.fetchCaringtypesRemote(
                caringtypeEntityRemoteList -> {
                    if (!caringtypeEntityRemoteList.isEmpty()) {
                        ArrayList<CaringtypeEntity> _caringtypes = new ArrayList<>();
                        caringtypeEntityRemoteList.forEach(caringtype -> _caringtypes.add(new CaringtypeEntity(caringtype)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringtypeDao().insertCaringtypes(_caringtypes),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Caringtypes Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caringtypes insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringtypeDao().selectCaringtypes();
    }
    public LiveData<CaringtypeEntity> getCaringtype(int id){
        mRemoteDataSource.fetchCaringtypeRemote(id,
                caringtypeEntityRemote -> {
                    if (caringtypeEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringtypeDao().insertCaringtype(new CaringtypeEntity(caringtypeEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Caringtypes Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caringtypes insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringtypeDao().selectCaringtype(id);
    }
    public LiveData<Long> addCaringtype(CaringtypeEntity caringtype){
        MutableLiveData<Long> nonce = new MutableLiveData<>();
        mRemoteDataSource.createCaringtypeRemote(new CaringtypeEntityRemote(caringtype),
                caringtypeEntityRemote -> {
                    if (caringtypeEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringtypeDao().insertCaringtype(new CaringtypeEntity(caringtypeEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Caringtype Inserted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caringtype insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> editCaringtype(CaringtypeEntity caringtype){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.updateCaringtypeRemote(new CaringtypeEntityRemote(caringtype),
                caringtypeEntityRemote -> {
                    if (caringtypeEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringtypeDao().updateCaringtype(new CaringtypeEntity(caringtypeEntityRemote)),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Caringtype Updated successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caringtype Update failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> deleteCaringtype(int id){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.deleteCaringtypeRemote(id,
                caringtypeRemote -> {
                    if (caringtypeRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringtypeDao().deleteCaringtype(id),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Caringtype Deleted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caringtype Delete failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<List<CaringtypeEntity>> findCaringtypes(String query) {
        return mLocalDatabase.CaringtypeDao().SearchCaringtypes(query);
    }

    /** Patient Repository Methods **/
    public LiveData<List<PatientEntity>> getPatients() {
        mRemoteDataSource.fetchPatientsRemote(
                patientEntityRemoteList -> {
                    if (!patientEntityRemoteList.isEmpty()) {
                        ArrayList<PatientEntity> _patients = new ArrayList<>();
                        patientEntityRemoteList.forEach(patient -> _patients.add(new PatientEntity(patient)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.PatientDao().insertPatients(_patients),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Patients Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Patients insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.PatientDao().selectPatients();
    }
    public LiveData<PatientEntity> getPatient(int id){
        mRemoteDataSource.fetchPatientRemote(id,
                patientEntityRemote -> {
                    if (patientEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.PatientDao().insertPatient(new PatientEntity(patientEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Patient was found");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Patient wasn't found");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.PatientDao().selectPatient(id);
    }
    public LiveData<Long> addPatient(PatientEntity patient){
        MutableLiveData<Long> nonce = new MutableLiveData<>();
        mRemoteDataSource.createPatientRemote(new PatientEntityRemote(patient),
                patientEntityRemote -> {
                    if (patientEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.PatientDao().insertPatient(new PatientEntity(patientEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Patient Inserted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Patient insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> editPatient(PatientEntity patient){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.updatePatientRemote(new PatientEntityRemote(patient),
                patientEntityRemote -> {
                    if (patientEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.PatientDao().updatePatient(new PatientEntity(patientEntityRemote)),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Patient updated successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Patient update failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Boolean> deletePatient(int id){
        MutableLiveData<Boolean> nonce = new MutableLiveData<>();
        mRemoteDataSource.deletePatientRemote(id,
                patientEntityRemote -> {
                    if (patientEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.PatientDao().deletePatient(id),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Patient Deleted successfully");
                                            nonce.postValue(true);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Patient Delete failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<List<PatientEntity>> findPatients(String query) {
        return mLocalDatabase.PatientDao().SearchPatients(query);
    }

    /** Caring Repository Methods **/
    public LiveData<List<CaringEntity>> getCarings() {
        mRemoteDataSource.fetchCaringsRemote(
                caringEntityRemoteList -> {
                    if (!caringEntityRemoteList.isEmpty()) {
                        ArrayList<CaringEntity> _carings = new ArrayList<>();
                        caringEntityRemoteList.forEach(caringtype -> _carings.add(new CaringEntity(caringtype)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCarings(_carings),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Carings Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Carings insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectCarings();
    }
    public LiveData<List<CaringEntity>> getTrashedCarings(){
        mRemoteDataSource.fetchTrashedCaringsRemote(
                caringEntityRemoteList -> {
                    if (!caringEntityRemoteList.isEmpty()) {
                        ArrayList<CaringEntity> _carings = new ArrayList<>();
                        caringEntityRemoteList.forEach(caringtype -> _carings.add(new CaringEntity(caringtype)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCarings(_carings),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Trashed Carings Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Trashed Carings insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectTrashedCarings();
    }
    public LiveData<Integer> editTrashedCaring(CaringEntity caring){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.updateTrashedCaringRemote(new CaringEntityRemote(caring),
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().updateCaring(new CaringEntity(caringEntityRemote)),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Trashed Caring Updated successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Trashed Caring Update failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<CaringEntity> getTrashedCaring(int id){
        mRemoteDataSource.fetchTrashedCaringRemote(id,
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCaring(new CaringEntity(caringEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Trashed Caring Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Trashed Caring insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectTrashedCaring(id);
    }
    public LiveData<Integer> pruneCaring(int id){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.pruneCaringRemote(id,
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().deleteCaring(id),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Caring pruned successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caring prune failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<CaringEntity> getCaring(int id){
        mRemoteDataSource.fetchCaringRemote(id,
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCaring(new CaringEntity(caringEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Caring Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caring insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectCaring(id);
    }
    public LiveData<List<CaringEntity>> getCaringsByNurseId(int nurseid){
        Log.d(TAG,"nurseid : "+String.valueOf(nurseid));
        mRemoteDataSource.fetchCaringsByNurseIdRemote(nurseid,
                caringEntityRemoteList -> {
                    if (caringEntityRemoteList !=null) {
                        ArrayList<CaringEntity> _carings = new ArrayList<>();
                        caringEntityRemoteList.forEach(caring -> _carings.add(new CaringEntity(caring)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCarings(_carings),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Carings Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Carings insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectCaringsByNurseId(nurseid);
    }
    public LiveData<List<CaringEntity>> getCaringsByCaringtypeId(int caringtypeId){
        mRemoteDataSource.fetchCaringsByCaringtypeIdRemote(caringtypeId,
                caringEntityRemoteList -> {
                    if (caringEntityRemoteList !=null) {
                        ArrayList<CaringEntity> _carings = new ArrayList<>();
                        caringEntityRemoteList.forEach(caring -> _carings.add(new CaringEntity(caring)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCarings(_carings),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Carings Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Carings insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectCaringsByCaringtypeId(caringtypeId);
    }
    public LiveData<List<CaringEntity>> getCaringsByPatientId(int PatientId){
        mRemoteDataSource.fetchCaringsByPatientIdRemote(PatientId,
                caringEntityRemoteList -> {
                    if (caringEntityRemoteList !=null) {
                        ArrayList<CaringEntity> _carings = new ArrayList<>();
                        caringEntityRemoteList.forEach(caring -> _carings.add(new CaringEntity(caring)));
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCarings(_carings),
                                    new FutureCallback<List<Long>>() {
                                        @Override
                                        public void onSuccess(List<Long> result) {
                                            Log.d(TAG,"Carings Inserted successfully");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Carings insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });
        return mLocalDatabase.CaringDao().selectCaringsByPatientId(PatientId);
    }
    public LiveData<Long> addCaring(CaringEntity caring){
        MutableLiveData<Long> nonce = new MutableLiveData<>();
        mRemoteDataSource.createCaringRemote(new CaringEntityRemote(caring),
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().insertCaring(new CaringEntity(caringEntityRemote)),
                                    new FutureCallback<Long>() {
                                        @Override
                                        public void onSuccess(Long result) {
                                            Log.d(TAG,"Caring Inserted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caring insertion failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> editCaring(CaringEntity caring){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.updateCaringRemote(new CaringEntityRemote(caring),
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().updateCaring(new CaringEntity(caringEntityRemote)),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Caring Updated successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caring Update failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<Integer> deleteCaring(int id){
        MutableLiveData<Integer> nonce = new MutableLiveData<>();
        mRemoteDataSource.deleteCaringRemote(id,
                caringEntityRemote -> {
                    if (caringEntityRemote !=null) {
                        if (Boolean.TRUE.equals(mLocalDatabase.getDatabaseCreated().getValue())) {
                            Futures.addCallback(mLocalDatabase.CaringDao().deleteCaring(id),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG,"Caring Deleted successfully");
                                            nonce.postValue(result);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,"Caring Delete failed");
                                        }
                                    },new AppExecutors().diskIO());
                        }
                    }
                });

        return nonce;
    }
    public LiveData<List<CaringEntity>> findCarings(String query) {
        return mLocalDatabase.CaringDao().SearchCarings(query);
    }
    public LiveData<List<CaringEntity>> findCaringsByNurseId(String query, int nurseid) {
        return mLocalDatabase.CaringDao().SearchCaringsByNurseId(query, nurseid);
    }
}
