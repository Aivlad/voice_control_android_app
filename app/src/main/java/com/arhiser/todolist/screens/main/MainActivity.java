package com.arhiser.todolist.screens.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import com.arhiser.todolist.R;
import com.arhiser.todolist.model.Note;
import com.arhiser.todolist.screens.details.NoteDetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // переменная для списка

    private TextToSpeech textToSpeech;
    private SoundPool sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.list); // привязка к объекту
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);  // определяем размещение внутри списка
        recyclerView.setLayoutManager(linearLayoutManager); // вешаем менеджер
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));    // добавляем разделители м-ду эл-тами

        final Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteDetailsActivity.start(MainActivity.this, null);
            }
        });

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getNoteLiveData().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setItems(notes);
            }
        });

//        createSoundPool();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        FloatingActionButton vce = findViewById(R.id.vce);
        vce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickMicrophone(view);
            }
        });
    }

    /**
     * Нажатие кнопки "Микрофон"
     * */
    public void onClickMicrophone(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);   // создаем сообщение к системе, в котором указываем, что хотим запустить
        // RECOGNIZE SPEECH (распознование голоса)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  // определяем настройку модели языка, которая будет иметь FREE FORM
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());  // настраиваем язык: getDefault - приявязка к языку смартфона (тот, который по дефолту на смартфоне)
        startActivityForResult(intent, 10); // отправка сообщения и определяем ожидание результата с кодом 10 (случайное число, можно указать любое)
                                                        // теперь сообщение будет привязано к указанному коду
    }

    // onActivityResult - функция, которая ждет результата activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  // дефолтное определение

        System.out.println(requestCode);
        if (resultCode == RESULT_OK && data != null) { // RESULT_OK - все произошло хорошо, data != null - данные не пустые
            switch (requestCode) {
                case 10:
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);  // берем все данные, которые пришли
                    textCommand(text.get(0));
                    break;
            }
        }
        else {
            callToast("Не удалось распознать речь!");
        }
    }

    /**
     * Вызов тоста
     * */
    private void callToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Исполнение текстовых команд
     * */
    private void textCommand(String command) {
        callToast(command);
//        switch (command.toLowerCase()) {
//            case "яблоко":
//                imMain.setImageResource(R.drawable.apple);
//                break;
//            case "дыня":
//                imMain.setImageResource(R.drawable.melone);
//                break;
//            case "арбуз":
//                imMain.setImageResource(R.drawable.watermelone);
//                break;
//            case "корова":
//                imMain.setImageResource(R.drawable.cow);
//                break;
//            case "протокол самоуничтожение":
//            case "протокол самоуничтожения":
//            case "протокол сама уничтожение":
//            case "протокол сама уничтожения":
//                selfDestructProtocol();
//                break;
//            case "давай попытку":
//                textToSpeech.speak("Максим Валентинович, один великий герой мультфильма сказал: ладно, и так сойдет. Может быть и это приложение сойдет для сдачи?",
//                        TextToSpeech.QUEUE_FLUSH, null);
//                break;
//            case "выход":
//                finish();
//                break;
//            default:
//                imMain.setImageResource(R.drawable.question);
//                textToSpeech.speak("Моя твоя не понимать",
//                        TextToSpeech.QUEUE_FLUSH, null);
//        }
    }


//    // звук
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    protected  void createNewSoundPool() {
//        AudioAttributes attributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_GAME)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();
//        sounds = new SoundPool.Builder()
//                .setAudioAttributes(attributes)
//                .build();
//    }
//
//    @SuppressWarnings("deprecation")
//    protected void createOldSoundPool() {
//        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    protected void  createSoundPool() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            createNewSoundPool();
//        }
//        else {
//            createOldSoundPool();
//        }
//    }
}
