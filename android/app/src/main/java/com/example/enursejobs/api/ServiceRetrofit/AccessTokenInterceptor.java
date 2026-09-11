package com.example.enursejobs.api.ServiceRetrofit;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.example.enursejobs.AppExecutors;
import com.example.enursejobs.DataRepository;
import com.example.enursejobs.db.AppDatabase;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AccessTokenInterceptor implements Interceptor {

    private static final String TAG = "AccessTokenInterceptor";

    private final AppDatabase database;
    private String accessToken;

    public AccessTokenInterceptor(@NonNull Context context)
    {
        this.database=AppDatabase.getInstance(context.getApplicationContext(),new AppExecutors());
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        //get token from db
        Futures.addCallback(database.AccessTokenDao().selectToken(), new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG,"accessToken: "+ result);
                AccessTokenInterceptor.this.accessToken=result;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        }, new AppExecutors().diskIO());


        if(originalRequest.url().encodedPath().equals("auth/login")){
            // don't add token header
            return chain.proceed(chain.request());
        }

        // add token header
        Request request = newRequestWithAccessToken(chain.request(), accessToken);
        return chain.proceed(request);
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
