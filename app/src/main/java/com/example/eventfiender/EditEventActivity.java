package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.eventfiender.databinding.ActivityEditEventBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditEventActivity extends AppCompatActivity {

    private ActivityEditEventBinding binding;

    private DatabaseReference eventsDB;
    private String LIST_KEY = "BaseEvents";

    String eventName, eventDate, eventAge, eventInfo, eventID, videoLink, stadt, eventImage;
    List<ListEntity> events = new ArrayList<>();

    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String email;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();
        eventID = arguments.getString("eventID");

//        Иницализация бд
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("BaseEvents");
        databaseReference1.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Вставка из бд в EditText
                    binding.editEventName.setText(ds.child("event_name").getValue().toString());
                    binding.editEventDate.setText(ds.child("event_date").getValue().toString());
                    binding.editEventAge.setText(ds.child("event_age").getValue().toString());
                    binding.editEventInfo.setText(ds.child("event_info").getValue().toString());
                    binding.editVideoLink.setText("youtu.be/" + ds.child("videoLink").getValue().toString());
                    binding.editStadt.setText(ds.child("stadt").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
//          Нажатие на кнопку подтверждения
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
                eventName = String.valueOf(binding.editEventName.getText());
                eventDate = String.valueOf(binding.editEventDate.getText());
                eventAge = String.valueOf(binding.editEventAge.getText());
                eventInfo = String.valueOf(binding.editEventInfo.getText());
                videoLink = String.valueOf(binding.editVideoLink.getText());
                stadt = String.valueOf(binding.editStadt.getText());

                eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
//                Загрузка на сервер данных
                eventsDB.child(eventID).child("0").child("event_name").setValue(eventName);
                eventsDB.child(eventID).child("0").child("event_date").setValue(eventDate);
                eventsDB.child(eventID).child("0").child("event_age").setValue(eventAge);
                eventsDB.child(eventID).child("0").child("event_info").setValue(eventInfo);
                eventsDB.child(eventID).child("0").child("videoLink").setValue(videoLink);
                eventsDB.child(eventID).child("0").child("stadt").setValue(stadt);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}