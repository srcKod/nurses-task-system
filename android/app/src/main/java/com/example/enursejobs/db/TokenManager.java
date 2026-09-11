//package com.example.enursejobs.db;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.datastore.preferences.core.MutablePreferences;
//import androidx.datastore.preferences.core.Preferences;
//import androidx.datastore.preferences.core.PreferencesKeys;
//import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
//import androidx.datastore.rxjava2.RxDataStore;
//
//import com.example.enursejobs.api.ServiceRetrofit.RetrofitDataSource;
//
//import org.jetbrains.annotations.NotNull;
//import org.reactivestreams.Subscription;
//
//import io.reactivex.Flowable;
//import io.reactivex.FlowableSubscriber;
//import io.reactivex.Single;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.observers.DisposableSingleObserver;
//import io.reactivex.schedulers.Schedulers;
//
//public class TokenManager
//{
//    private static TokenManager instance = null;
//    RxDataStore<Preferences> dataStore;
//
//    public TokenManager(Context context){
//        this.dataStore =
//                new RxPreferenceDataStoreBuilder(context, /*name=*/ "settings").build();
////
//        // Key for saving String value
//        Preferences.Key<String> TOKEN_KEY = PreferencesKeys.stringKey("access_token");
//
//        // Storing an String value to DataStore with the key of TOKEN_KEY
//        saveString(TOKEN_KEY, "klsdjfkdkald");
//
//        // Read the data immediately as it's already observed
////        observeString(SOME_KEY, value -> runOnUiThread(() ->
////                Toast.makeText(this, "Observed Value: " + value, Toast.LENGTH_SHORT).show()));
//
//
//        // Wait some time before reading the data as it takes time until it's stored;
//        // so don't call it here, but on some other event
////        readInt(SOME_KEY, value -> runOnUiThread(() ->
////                Toast.makeText(this, "Value: " + value, Toast.LENGTH_SHORT).show()));
//
//    }
//
//    public static synchronized TokenManager getInstance(Context context){
//        if (null == instance)
//            instance = new TokenManager(context);
//        return instance;
//    }
//
//    /**
//     * Saving an String value to DataStore with some key
//     *
//     * @param key:   The key associated to the value need to be stored
//     * @param value: The value to be stored
//     */
//    public void saveString(Preferences.Key<String> key, String value) {
//        dataStore.updateDataAsync(prefsIn -> {
//            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
//            mutablePreferences.set(key, value);
//            return Single.just(mutablePreferences);
//        }).subscribe();
//
//    }
//
//    /**
//     * Returning String value from the DataStore which is associated to some key,
//     * once the result is returned, the subscription is disposed.
//     *
//     * @param key:      The key associated to the value need to be stored
//     * @param listener: The value is returned in a worker thread, and returned to the
//     *                  caller using a listener pattern
//     */
//    public void readString(Preferences.Key<String> key, StringListener listener) {
//        Flowable<String> flowable =
//                dataStore.data().map(prefs -> prefs.get(key));
//
//        flowable.firstOrError().subscribeWith(new DisposableSingleObserver<String>() {
//
//            @Override
//            public void onSuccess(@NotNull String value) {
//                listener.StringValue(value);
//            }
//
//            @Override
//            public void onError(@NotNull Throwable error) {
//                error.printStackTrace();
//            }
//        }).dispose();
//    }
//
//    /**
//     * Subscribing an observer to an int value in the DataStore which is associated to some key,
//     * The subscription submits any change to the value
//     *
//     * @param key:      The key associated to the value need to be stored
//     * @param listener: The value is returned in a worker thread, and returned to the
//     *                  caller using a listener pattern
//     */
//    public void observeString(Preferences.Key<String> key, StringListener listener) {
//        Flowable<String> flowable =
//                dataStore.data().map(prefs -> prefs.get(key));
//
//        flowable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()) // AndroidSchedulers requires ` implementation "io.reactivex.rxjava3:rxandroid:3.0.0" `
//                .subscribe(new FlowableSubscriber<String>() {
//
//                    @Override
//                    public void onSubscribe(@NonNull Subscription s) {
//                        s.request(Long.MAX_VALUE);
//                    }
//
//                    @Override
//                    public void onNext(String value) {
//                        listener.StringValue(value);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        t.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
//    }
//
//    public interface StringListener {
//        void StringValue(String value);
//    }
//}
