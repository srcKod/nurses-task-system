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
import com.example.enursejobs.R;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

public class NurseViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;
    private final LiveData<NurseEntity> mNurseObservable;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    final DataRepository mRepository;
    public NurseViewModel(Application application, @NonNull DataRepository repository, final int NurseId) {
        super(application);
        mRepository = repository;
        mNurseObservable = mRepository.getNurse(NurseId);
        mText = new MutableLiveData<>();
        mText.setValue("Nurse Info");
    }

    public LiveData<String> getText() {
        return mText;
    }

    /** Expose the LiveData Nurse query so the UI can observe it. **/
    public LiveData<NurseEntity> getNurse() {
        return mNurseObservable;
    }
    /** Remove Nurse **/
    public LiveData<Boolean> removeNurse(int nurseId) {
        return mRepository.deleteNurse(nurseId);
    }

    /** Edit Nurse **/
    public LiveData<Integer> editNurse(NurseEntity nurse){return mRepository.editNurse(nurse);}

    /**
     * A creator is used to inject the Nurse ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the Nurse ID can be passed in a public method.
     */

    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        private final Application mApplication;
        private final int mNurseId;
        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int nurseId) {
            mApplication = application;
            mNurseId = nurseId;
            mRepository = ((Enurse) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NurseViewModel(mApplication, mRepository, mNurseId);
        }
    }
}
