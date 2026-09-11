package com.example.enursejobs.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.enursejobs.DataRepository;
import com.example.enursejobs.Enurse;
import com.example.enursejobs.db.EntityLocal.AuthEntity;

public class LoginViewModel extends AndroidViewModel
{
    private final SavedStateHandle savedStateHandle;
    final DataRepository mRepository;
    public LoginViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        mRepository = ((Enurse) application).getRepository();
    }

    public LiveData<String> login(AuthEntity authEntity) {
       return mRepository.login(authEntity);
    }
}
