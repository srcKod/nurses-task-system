package com.example.enursejobs.api.ServiceRetrofit;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.example.enursejobs.AppExecutors;
import com.example.enursejobs.db.AppDatabase;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class Authenticator implements okhttp3.Authenticator {

    private static final String TAG = "Authenticator";
    private final AppDatabase database;
    private String accessToken;
    private String newAccessToken;
    private String refreshedAccessToken;
    private Request request = null;

    public Authenticator(@NonNull Context context)
    {
        this.database=AppDatabase.getInstance(context.getApplicationContext(),new AppExecutors());
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        //get token from db
        Futures.addCallback(database.AccessTokenDao().selectToken(),
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "saved token is: "+ result);
                        Authenticator.this.accessToken=result;
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        Log.d(TAG,t.getMessage());
                    }
                }, new AppExecutors().diskIO());

        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null;
        }

        // Access token is refreshed in another thread.
        Futures.addCallback(database.AccessTokenDao().selectToken(),
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "saved token is: "+ result);
                        Authenticator.this.newAccessToken=result;
                        synchronized (this) {
                            if (!accessToken.equals(newAccessToken)) {
                                Authenticator.this.request=newRequestWithAccessToken(response.request(), newAccessToken);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        Log.d(TAG,t.getMessage());
                    }
                }, new AppExecutors().diskIO());

        if(request != null){ return Authenticator.this.request; }

//        synchronized (this) {
//            String newAccessToken=database.AccessTokenDao().selectToken();
//            Log.d(TAG,"newAccessToken: "+ newAccessToken);
//
//            // Access token is refreshed in another thread.
//            if (!accessToken.equals(newAccessToken)) {
//                return newRequestWithAccessToken(response.request(), newAccessToken);
//            }

            // Need to refresh an access token
            RetrofitDataSource.getInstance().refreshAccessTokenRemote(newAccessToken,
                    refreshedToken -> {
                        if(refreshedToken != null){
                            Log.d(TAG, "token refreshed!: "+refreshedToken.getAccessToken());
                            Authenticator.this.refreshedAccessToken=refreshedToken.getAccessToken();
                            //update token in db
                            Futures.addCallback(database.AccessTokenDao().updateToken(refreshedToken.getAccessToken()),
                                    new FutureCallback<Integer>() {
                                        @Override
                                        public void onSuccess(Integer result) {
                                            Log.d(TAG, "refreshedToken updated in database: "+refreshedToken);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.d(TAG,t.getMessage());
                                        }
                                    }, new AppExecutors().diskIO());


//                            database.AccessTokenDao().updateToken(refreshedToken.getAccessToken());
//                            Log.d(TAG,"refreshedAccessToken: "+ refreshedToken.getAccessToken());
                        }
                    });

//            String refreshedAccessToken = database.AccessTokenDao().selectToken();
            return newRequestWithAccessToken(response.request(), refreshedAccessToken);
    }

    private boolean isRequestWithAccessToken(@NonNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
