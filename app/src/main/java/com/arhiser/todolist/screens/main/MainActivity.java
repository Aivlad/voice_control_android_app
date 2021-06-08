package com.arhiser.todolist.screens.main;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arhiser.todolist.App;
import com.arhiser.todolist.R;
import com.arhiser.todolist.model.Note;
import com.arhiser.todolist.screens.details.NoteDetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // переменная для списка

    private TextToSpeech textToSpeech;
    private SoundPool sounds;

    private final static int RS_CODE_CLICK_MICROPHONE = 10;
    private final static int RS_CODE_INDEX_DEFINITION = 11;
    private final static int RS_CODE_VOICE_NOTE = 12;


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
        localRecognizeSpeech(RS_CODE_CLICK_MICROPHONE);
    }

    /**
     * Определение индекса
     * */
    private void indexDefinition() {
        localRecognizeSpeech(RS_CODE_INDEX_DEFINITION);
    }

    /**
     * Голосовая заметка
     * */
    private void voiceNote() {
        localRecognizeSpeech(RS_CODE_VOICE_NOTE);
    }

    /**
     * Определение Recognize Speech вынесено в отдельный метод
     * */
    public void localRecognizeSpeech(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);   // создаем сообщение к системе, в котором указываем, что хотим запустить
        // RECOGNIZE SPEECH (распознование голоса)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  // определяем настройку модели языка, которая будет иметь FREE FORM
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());  // настраиваем язык: getDefault - приявязка к языку смартфона (тот, который по дефолту на смартфоне)
        startActivityForResult(intent, requestCode); // отправка сообщения и определяем ожидание результата с кодом requestCode (случайное число, можно указать любое)
                                                     // теперь сообщение будет привязано к указанному коду
    }


    // onActivityResult - функция, которая ждет результата activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  // дефолтное определение

        System.out.println(requestCode);
        if (resultCode == RESULT_OK && data != null) { // RESULT_OK - все произошло хорошо, data != null - данные не пустые
            ArrayList<String> text = null;
            switch (requestCode) {
                case RS_CODE_CLICK_MICROPHONE:
                    text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);  // берем все данные, которые пришли
                    textCommand(text.get(0));
                    break;
                case RS_CODE_INDEX_DEFINITION:
                    text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);  // берем все данные, которые пришли
                    textCommandAddition(text.get(0));
                    break;
                case RS_CODE_VOICE_NOTE:
                    text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);  // берем все данные, которые пришли
                    saveNote(text.get(0));
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

    private void saveNote(String content) {
        Note newNote = new Note();
        newNote.text = content;
        newNote.done = false;
        newNote.timestamp = System.currentTimeMillis();
        App.getInstance().getNoteDao().insert(newNote);

        callToast("Заметка под запись создана");
    }

    /**
     * Дополнительная текстовая команда (определить номер элемента)
     * */
    private View textCommandAddition(String command) {
        command = command
                .replaceAll("один", "1")
                .replaceAll("два", "2")
                .replaceAll("три", "3")
                .replaceAll("четыре", "4")
                .replaceAll("пять", "5")
                .replaceAll("шесть", "6")
                .replaceAll("семь", "7")
                .replaceAll("восемь", "8")
                .replaceAll("девять", "9")
                .replaceAll("ноль", "0")
                .replaceAll(" ", "");

        int index;
        try {
            index = Integer.parseInt(command);
            index--;
        }
        catch (NumberFormatException e) {
            callToast("Номер не распознан");
            return null;
        }

        if (index < 0 || index >= recyclerView.getAdapter().getItemCount()) {
            callToast("Такого номера нет");
            return null;
        }
        return recyclerView.findViewHolderForAdapterPosition(index).itemView;
    }


    /**
     * Исполнение текстовых команд
     * */
    private void textCommand(String command) {
        switch (command.toLowerCase().trim()) {
            case "создать заметку":
                callToast("Ручное создание заметки");
                NoteDetailsActivity.start(MainActivity.this, null);
                break;
            case "слушать":
                voiceNote();
                break;
            default:
                if (command.startsWith("найти")) {
                    View answer = textCommandAddition(command.substring("найти".length() + 1));
                    if (answer != null) {
                        callToast("Ручное обновление заметки");
                        answer.performClick();
                    }
                }
                else if (command.startsWith("выполнить")) {
                    String number = command.substring("выполнить".length() + 1);
                    View answer = textCommandAddition(number);
                    if (answer != null) {
                        callToast(String.format("Заметка %s выполнена", number));
                        answer.findViewById(R.id.completed).performClick();
                    }
                }
                else if (command.startsWith("удалить")) {
                    String number = command.substring("удалить".length() + 1);
                    View answer = textCommandAddition(number);
                    if (answer != null) {
                        callToast(String.format("Заметка %s удалена", number));
                        answer.findViewById(R.id.delete).performClick();
                    }
                }
                else
                    callToast("Команда не определена");
        }

        //region Fragment of meaningless commands
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
        //endregion
    }


    //region Sound setting (for connecting external tracks)
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
    //endregion
}
