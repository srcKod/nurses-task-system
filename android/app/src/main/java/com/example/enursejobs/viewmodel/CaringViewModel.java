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
import com.example.enursejobs.db.EntityLocal.CaringEntity;

public class CaringViewModel extends AndroidViewModel {
    private final LiveData<CaringEntity> mCaringObservable;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    final DataRepository mRepository;
    public CaringViewModel(Application application, @NonNull DataRepository repository, final int CaringId) {
        super(application);
        mRepository = repository;
        mCaringObservable = mRepository.getCaring(CaringId);
        mText = new MutableLiveData<>();
        mText.setValue("Caring Info");
    }

    public LiveData<String> getText() {
        return mText;
    }
    /** Expose the LiveData Caring query so the UI can observe it. **/
    public LiveData<CaringEntity> getCaring() {
        return mCaringObservable;
    }
    public LiveData<Integer> removeCaring(int caringId) {
        return mRepository.deleteCaring(caringId);
    }
    public LiveData<Integer> editCaring(CaringEntity caring){return mRepository.editCaring(caring);}

    /**
     * A creator is used to inject the Caring ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the Caring ID can be passed in a public method.
     */

    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        private final Application mApplication;
        private final int mCaringId;
        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int caringId) {
            mApplication = application;
            mCaringId = caringId;
            mRepository = ((Enurse) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CaringViewModel(mApplication, mRepository, mCaringId);
        }
    }
}
