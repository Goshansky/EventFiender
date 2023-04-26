package com.example.eventfiender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.eventfiender.databinding.ActivityCreateBinding;
import com.example.eventfiender.ui.My.MyFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private DatabaseReference eventsDB;
    private String LIST_KEY = "BaseEvents";

    String eventName, eventType, userID, info;
    List<ListEntity> events = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, MainActivity.class);

                eventName = String.valueOf(binding.editTextFirst.getText());
                eventType = String.valueOf(binding.editTextSecond.getText());
                //eventName = binding.editTextFirst.getText().toString();
                //eventType = binding.editTextSecond.getText().toString();

                eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
                events.add(new ListEntity(
                        eventName,
                        eventType
                        ));
                eventsDB.push().setValue(events);

//                try {
//                    Thread.sleep(2000); // задержка на 2 секунд
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}