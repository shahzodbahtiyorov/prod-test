package org.finance.data.room;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/** @noinspection ALL*/
public class DataBaseClient {

    private static DataBaseClient instance;

    private final SuperAppDatabase appDatabase;

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE cards ADD COLUMN new_column INTEGER DEFAULT 0");
        }
    };

    private DataBaseClient(Context context, String userId) {
        appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        SuperAppDatabase.class,
                        "user_db_" + userId)
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    public static synchronized DataBaseClient getInstance(Context context, String userId) {
        if (instance == null || !instance.getAppDatabase().getOpenHelper().getDatabaseName().equals("user_db_" + userId)) {
            instance = new DataBaseClient(context, userId);
        }
        return instance;
    }

    public SuperAppDatabase getAppDatabase() {
        return appDatabase;
    }
}
