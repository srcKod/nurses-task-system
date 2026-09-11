package com.example.enursejobs.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.enursejobs.DataRepository;
import com.example.enursejobs.Enurse;
import com.example.enursejobs.db.EntityLocal.AuthEntity;
import com.example.enursejobs.db.EntityLocal.CaringEntity;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private static final String QUERY_KEY = "QUERY";
    private final SavedStateHandle savedStateHandle;
    private LiveData<List<CaringEntity>> mCaringsObservable;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    final DataRepository mRepository;

    public HomeViewModel(Application application, @NonNull DataRepository repository,
                         @NonNull SavedStateHandle savedStateHandle, final int NurseId) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        mRepository = repository;

        mCaringsObservable = Transformations.switchMap(
                savedStateHandle.getLiveData(QUERY_KEY, null),
                query -> {
                    String queryString = (String) query; // cast the query to String type
                    if (TextUtils.isEmpty(queryString)) {
                        return mRepository.getCaringsByNurseId(NurseId);
                    }
                    return mRepository.findCaringsByNurseId("*" + queryString + "*", NurseId);
                });
    }

    public LiveData<AuthEntity> getProfile() {
        return mRepository.getProfile();
    }

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        savedStateHandle.set(QUERY_KEY, query);
    }

    public LiveData<List<CaringEntity>> getCarings() {
        return mCaringsObservable;
    }

    public static class Factory extends AbstractSavedStateViewModelFactory {

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
        @NonNull
        @Override
        protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass,
                                                 @NonNull SavedStateHandle handle) {
            return (T) new HomeViewModel(mApplication, mRepository, handle, mNurseId);
        }
    }
}
