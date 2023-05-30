package com.example.eventfiender;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eventfiender.databinding.ActivityCreateBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private DatabaseReference eventsDB; // бд событий
    private final String LIST_KEY = "BaseEvents"; // Название бд
    String eventName, eventDate, eventAge, eventInfo, videoLink, stadt;
    List<ListEntity> events = new ArrayList<>(); // Список параметров событий

    private String userID;
    private FirebaseAuth mAuth;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                // Считываем введённые данные
                eventName = String.valueOf(binding.editEventName.getText());
                eventDate = String.valueOf(binding.editEventDate.getText());
                eventAge = String.valueOf(binding.editEventAge.getText());
                eventInfo = String.valueOf(binding.editEventInfo.getText());
                videoLink = String.valueOf(binding.editVideoLink.getText());
                stadt = String.valueOf(binding.editStadt.getText());
                // Проверка на корректность введённых данных
                if (!eventName.isEmpty() &
                        !eventDate.isEmpty() &
                        !eventAge.isEmpty() &
                        !eventInfo.isEmpty() &
                        !stadt.isEmpty() &
                        (videoLink.contains("youtu.be/") | videoLink.contains("youtube.com/watch?v="))) {
                    // Обрезаем ненужную часть ссылки на видео
                    videoLink = videoLink.subSequence(videoLink.length()-11, videoLink.length()).toString();
                    mAuth = FirebaseAuth.getInstance();
                    email = mAuth.getCurrentUser().getEmail();
                    userID = mAuth.getCurrentUser().getUid();
                    // Заносим данные в бд
                    eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
                    events.add(new ListEntity(eventsDB.getKey(),
                            eventName,
                            eventDate,
                            eventAge,
                            eventInfo,
                            userID,
                            email,
                            videoLink,
                            stadt
                            ));
                    eventsDB.push().setValue(events);
                    // Выходим из активности
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(CreateActivity.this, "Некорректные данные", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}