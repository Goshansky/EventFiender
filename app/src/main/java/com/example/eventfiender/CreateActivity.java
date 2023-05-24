package com.example.eventfiender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.eventfiender.databinding.ActivityCreateBinding;
import com.example.eventfiender.ui.My.MyFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private DatabaseReference eventsDB;
    private String LIST_KEY = "BaseEvents";

    String eventName, eventDate, eventAge, eventInfo, eventID, videoLink;
    List<ListEntity> events = new ArrayList<>();

    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
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

                eventName = String.valueOf(binding.editEventName.getText());
                eventDate = String.valueOf(binding.editEventDate.getText());
                eventAge = String.valueOf(binding.editEventAge.getText());
                eventInfo = String.valueOf(binding.editEventInfo.getText());
                videoLink = String.valueOf(binding.editVideoLink.getText());


                videoLink = videoLink.subSequence(videoLink.length()-11, videoLink.length()).toString();
                mAuth = FirebaseAuth.getInstance();
                email = mAuth.getCurrentUser().getEmail();
                userID = mAuth.getCurrentUser().getUid();

                eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
                events.add(new ListEntity(
                        eventName,
                        eventDate,
                        eventAge,
                        eventInfo,
                        userID,
                        email,
                        videoLink
                        ));
                eventsDB.push().setValue(events);

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