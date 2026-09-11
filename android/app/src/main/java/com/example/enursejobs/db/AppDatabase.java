package com.example.enursejobs.db;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.enursejobs.AppExecutors;
import com.example.enursejobs.db.Converter.DateConverter;
import com.example.enursejobs.db.Dao.AccessTokenDao;
import com.example.enursejobs.db.Dao.AuthDao;
import com.example.enursejobs.db.Dao.CaringDao;
import com.example.enursejobs.db.Dao.CaringtypeDao;
import com.example.enursejobs.db.Dao.NurseDao;
import com.example.enursejobs.db.Dao.PatientDao;
import com.example.enursejobs.db.EntityLocal.AccessTokenEntity;
import com.example.enursejobs.db.EntityLocal.AuthEntity;
import com.example.enursejobs.db.EntityLocal.CaringEntity;
import com.example.enursejobs.db.EntityLocal.CaringFtsEntity;
import com.example.enursejobs.db.EntityLocal.CaringtypeEntity;
import com.example.enursejobs.db.EntityLocal.CaringtypeFtsEntity;
import com.example.enursejobs.db.EntityLocal.NurseEntity;
import com.example.enursejobs.db.EntityLocal.NurseFtsEntity;
import com.example.enursejobs.db.EntityLocal.PatientEntity;
import com.example.enursejobs.db.EntityLocal.PatientFtsEntity;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {AccessTokenEntity.class, AuthEntity.class,
                      NurseEntity.class, NurseFtsEntity.class,
                      CaringEntity.class, CaringFtsEntity.class,
                      CaringtypeEntity.class, CaringtypeFtsEntity.class,
                      PatientEntity.class, PatientFtsEntity.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase
{
    private static final String TAG = "AppDatabase";
    @VisibleForTesting
    public static final String DATABASE_NAME = "ENURSEDB";

    /** "volatile" keyword used when the value of an attribute is not cached thread-locally, and is always read from the "main memory" **/
    private static volatile AppDatabase sInstance = null;

    /** Data Access Objects Implementation **/
    public abstract AccessTokenDao AccessTokenDao();
    public abstract AuthDao AuthDao();
    public abstract NurseDao NurseDao();
    public abstract CaringtypeDao CaringtypeDao();
    public abstract CaringDao CaringDao();
    public abstract PatientDao PatientDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();
    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    //this is so you don't need to pass context each time
    public static synchronized AppDatabase getInstance(){
        if (null == sInstance)
        {
            Log.d(TAG,  " is not initialized, call getInstance(...) first");
            throw new IllegalStateException(AppDatabase.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return sInstance;
    }

    /** DataBase Builder ..\
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     **/
    @NonNull
    private static AppDatabase buildDatabase(final Context applicationContext, final AppExecutors executors) {
        Log.d(TAG,"buildDatabase has been Invoked.");
        AppDatabase db;
        db = Room.databaseBuilder(applicationContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Log.d(TAG,"Database has been created.");
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
                            addDelay();
                            List<AuthEntity> _Auths = new ArrayList<>();
                            _Auths.add(new AuthEntity(0,0,"testname","test@mail.com","testpass"));
                            AppDatabase database = AppDatabase.getInstance(applicationContext, executors);

                            // notify that the database was created and it's ready to be used

                            insertData(database, _Auths);
                            database.setDatabaseCreated();

                        });
                    }
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        Log.d(TAG,"Database has been opened.");
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build();
        db.getOpenHelper().getWritableDatabase();
        Log.d(TAG,"Database getWritableDatabase");
        return db;
    }
    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }
    private void updateDatabaseCreated(@NonNull Context applicationContext){
        if (applicationContext.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }
    private static void insertData(@NonNull final AppDatabase database, final List<AuthEntity> _auths) {
            database.runInTransaction(() -> {
                    _auths.forEach(auth -> database.AuthDao().insertAuth(auth));
                     Log.d(TAG, "Data has been inserted.");
            });
    }
    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,"DATABASE MIGRATE INVOKED");

            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `nurse_Fts` USING FTS4(" +
                                " `name` TEXT, `email` TEXT, `phone` TEXT, content= `t_nurse` )");
            database.execSQL("INSERT INTO nurse_Fts (" +
                                " `rowid`, `name`, `email`, `phone`) " +
                                "  SELECT `id`, `name`, `email`, `phone`" +
                                "  FROM t_nurse");

            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `Caringtype_Fts` USING FTS4(" +
                                " `name` TEXT, `description` TEXT, content= `t_caringtype` )");
            database.execSQL("INSERT INTO Caringtype_Fts (`rowid`, `name` TEXT, `description` TEXT) " +
                                " SELECT `id`, `name`, `description` FROM t_caringtype");

            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `patient_Fts` USING FTS4(`name` TEXT, content= `t_patient` )");
            database.execSQL("INSERT INTO patient_Fts ( `rowid`, `name`) " +
                                " SELECT `id`, `name` FROM t_patient");

            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `caring_Fts` USING FTS4( `nurseName` TEXT, `caringtypeName` TEXT," +
                                " `patientName` TEXT, `status` TEXT, `time` TEXT, `description` TEXT content=`t_caring` )");
            database.execSQL("INSERT INTO caring_Fts (`rowid`, `nurseName`, `caringtypeName`, `patientName`, `status`," +
                                " `time`, `description`) " +
                                "  SELECT `id`, `nurseName`, `caringtypeName`, `patientName`, `status`," +
                                " `time`, `description` FROM t_caring");
        }
    };
}
