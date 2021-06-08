package com.arhiser.todolist.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.arhiser.todolist.model.Note;

/**
* entities - список сущностей БД
* */
@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // в теле определяются Dao для всех entities
    public abstract NoteDao noteDao();
}
