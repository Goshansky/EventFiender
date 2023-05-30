package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.eventfiender.databinding.ActivityCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private DatabaseReference eventsDB;
    private String LIST_KEY = "BaseEvents";

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;


    String eventName, eventDate, eventAge, eventInfo, videoLink, stadt, eventImage;
    private String random_image;
    List<ListEntity> events = new ArrayList<>();

    private String userID;
    private FirebaseAuth mAuth;
    private String email;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
        }
    }

    private void uploadImage(){
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(CreateActivity.this);
            progressDialog.setTitle("Uploading...");
            random_image = UUID.randomUUID().toString();
            eventImage = random_image;
            StorageReference ref = storage.getReference().child("images/" + random_image);
            ref.putFile(filePath);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });



        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, MainActivity.class);

                eventName = String.valueOf(binding.editEventName.getText());
                eventDate = String.valueOf(binding.editEventDate.getText());
                eventAge = String.valueOf(binding.editEventAge.getText());
                eventInfo = String.valueOf(binding.editEventInfo.getText());
                videoLink = String.valueOf(binding.editVideoLink.getText());
                stadt = String.valueOf(binding.editStadt.getText());
                uploadImage();
                System.out.println(eventImage+"\n\n\n\n\n\n\n\n\n");
                if (eventImage == null) eventImage = "standartAva.png";

                videoLink = videoLink.subSequence(videoLink.length()-11, videoLink.length()).toString();
                mAuth = FirebaseAuth.getInstance();
                email = mAuth.getCurrentUser().getEmail();
                userID = mAuth.getCurrentUser().getUid();

                eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
                events.add(new ListEntity(eventsDB.getKey(),
                        eventName,
                        eventDate,
                        eventAge,
                        eventInfo,
                        userID,
                        email,
                        videoLink,
                        stadt,
                        eventImage
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