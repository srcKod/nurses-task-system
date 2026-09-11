package com.example.enursejobs;

import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.example.enursejobs.api.ServiceRetrofit.RetrofitDataSource;
import com.example.enursejobs.db.AppDatabase;

public class Enurse extends Application
{
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dynamic Colors Support
        DynamicColors.applyToActivitiesIfAvailable(this);

        mAppExecutors = new AppExecutors();
    }

     /** Database Instance **/
    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase(), RetrofitDataSource.getInstance(this));
    }
}
