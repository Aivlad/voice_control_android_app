package com.arhiser.todolist;

import android.app.Application;

import androidx.room.Room;

import com.arhiser.todolist.data.AppDatabase;
import com.arhiser.todolist.data.NoteDao;

public class App extends Application {

    private AppDatabase database;   // наша БД
    private NoteDao noteDao;    // наш data access object

    private static App instance;    // текущий instance
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        /*
        * создание БД:
        * databaseBuilder - создать БД (
        *   getApplicationContext() - контекст приложения
        *   AppDatabase.class - класс с описанием БД
        *   app-db-name - имя БД)
        *   allowMainThreadQueries - делать запросы к БД из основного потока (для простоты, в целом нерекомендуется так)
        * */
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db-name")
                .allowMainThreadQueries()
                .build();

        // получаем Dao (хоть он абстактный, но после определения БД мы получаем уже конкретную реализацию)
        noteDao = database.noteDao();
    }

    //region getters and setters (App Singleton (шаблон проектирования (для простоты)))
    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }
    //endregion
}
