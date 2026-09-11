package com.example.enursejobs.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.enursejobs.DataRepository;
import com.example.enursejobs.Enurse;
import com.example.enursejobs.db.EntityLocal.PatientEntity;

public class PatientViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;
    private final LiveData<PatientEntity> mPatientObservable;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    final DataRepository mRepository;
    public PatientViewModel(Application application, @NonNull DataRepository repository, final int PatientId) {
        super(application);
        mRepository = repository;
        mPatientObservable = mRepository.getPatient(PatientId);
        mText = new MutableLiveData<>();
        mText.setValue("Patient Info");
    }

    public LiveData<String> getText() {
        return mText;
    }

    /** Expose the LiveData Nurse query so the UI can observe it. **/
    public LiveData<PatientEntity> getPatient() {
        return mPatientObservable;
    }
    /** Remove Nurse **/
    public LiveData<Boolean> removePatient(int patientId) {
        return mRepository.deletePatient(patientId);
    }

    /** Edit Nurse **/
    public LiveData<Integer> editPatient(PatientEntity patient){return mRepository.editPatient(patient);}

    /**
     * A creator is used to inject the Nurse ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the Nurse ID can be passed in a public method.
     */

    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        private final Application mApplication;
        private final int mPatientId;
        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int patientId) {
            mApplication = application;
            mPatientId = patientId;
            mRepository = ((Enurse) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PatientViewModel(mApplication, mRepository, mPatientId);
        }
    }
}
