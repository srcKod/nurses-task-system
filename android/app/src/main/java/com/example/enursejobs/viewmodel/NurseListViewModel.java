package com.example.enursejobs.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.enursejobs.DataRepository;
import com.example.enursejobs.Enurse;
import com.example.enursejobs.db.EntityLocal.NurseEntity;

import java.util.List;

public class NurseListViewModel extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";
    final DataRepository mRepository;
    private final SavedStateHandle savedStateHandle;
    private final LiveData<List<NurseEntity>> mNurseEntity;
    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    public NurseListViewModel(@NonNull Application application,  @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        mRepository = ((Enurse) application).getRepository();

        // Use the savedStateHandle.getLiveData() as the input to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on what query the user has entered
        mNurseEntity = Transformations.switchMap(
                savedStateHandle.getLiveData(QUERY_KEY, null),
                query -> {
                    String queryString = (String) query; // cast the query to String type
                    if (TextUtils.isEmpty(queryString)) {
                        return mRepository.getNurses();
                    }
                    return mRepository.findNurses("*" + queryString + "*");
                });
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

    /**
     * Expose the LiveData Nurses query so the UI can observe it.
     */
    public LiveData<List<NurseEntity>> getNurses() {
        return mNurseEntity;
    }
}
