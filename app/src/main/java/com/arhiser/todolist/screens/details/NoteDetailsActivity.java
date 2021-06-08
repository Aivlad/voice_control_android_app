package com.arhiser.todolist.screens.details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arhiser.todolist.App;
import com.arhiser.todolist.R;
import com.arhiser.todolist.model.Note;

// NoteDetailsActivity - активити для редактирования и создания заметки
public class NoteDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteDetailsActivity.EXTRA_NOTE"; // ключ для передачи заметки ч-з bundle

    private Note note; // текущая заметка

    private EditText editText;  // текстовое поле ввода

    /**
     * Вызов одного активити из другого в обертке ввиде ф-ции
     * */
    public static void start(Activity caller, Note note) {
        Intent intent = new Intent(caller, NoteDetailsActivity.class);  // 1-источник, 2-класс вызова
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note); // прикрепляем note к нашему intent
        }
        caller.startActivity(intent); // запуск активити
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_details); // приложение читает файл разметки и создает уже конкретные классы

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);   // задаем Toolbar в качестве ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // кнопка назад
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getString(R.string.note_details_title));   // задаем текст заголовка

        editText = findViewById(R.id.text); // достаем editText

        if (getIntent().hasExtra(EXTRA_NOTE)) {
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            editText.setText(note.text);
        } else {
            note = new Note();
        }
    }

    /**
     * Ф-ция создания меню
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Обработка событий
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if (editText.getText().length() > 0) {
                    note.text = editText.getText().toString();
                    note.done = false;
                    note.timestamp = System.currentTimeMillis();
                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        App.getInstance().getNoteDao().update(note);
                    } else {
                        App.getInstance().getNoteDao().insert(note);
                    }
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
