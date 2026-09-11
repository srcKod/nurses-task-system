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
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

public class CaringtypeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;
    private final LiveData<CaringtypeEntity> mCaringtypeObservable;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    final DataRepository mRepository;

    public CaringtypeViewModel(Application application, @NonNull DataRepository repository, final int CaringtypeId) {
        super(application);
        mRepository = repository;
        mCaringtypeObservable = mRepository.getCaringtype(CaringtypeId);
        mText = new MutableLiveData<>();

        mText.setValue("Carying Type info");
    }

    public LiveData<String> getText() {
        return mText;
    }

    /** Expose the LiveData Carying Type query so the UI can observe it. **/
    public LiveData<CaringtypeEntity> getCaringtype() {
        return mCaringtypeObservable;
    }

    /** Remove Carying Type **/
    public LiveData<Integer> removeCaringtype(int caringtypeId) {
        return mRepository.deleteCaringtype(caringtypeId);
    }

    /** Edit Carying Type **/
    public LiveData<Integer> editCaringtype(CaringtypeEntity caringtype){return mRepository.editCaringtype(caringtype);}

    /**
     * A creator is used to inject the Carying Type ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the Carying Type ID can be passed in a public method.
     */

    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        private final Application mApplication;
        private final int mCaringtypeId;
        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int caringtypeId) {
            mApplication = application;
            mCaringtypeId = caringtypeId;
            mRepository = ((Enurse) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CaringtypeViewModel(mApplication, mRepository, mCaringtypeId);
        }
    }
}
