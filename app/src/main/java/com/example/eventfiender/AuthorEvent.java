package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.eventfiender.databinding.ActivityAuthorEventBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AuthorEvent extends AppCompatActivity {
    private ActivityAuthorEventBinding binding;
    private String eventName, eventDate, eventAge, eventInfo, myVideoYoutubeId, stadt, eventImage, eventID, email;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private final String LIST_KEY = "BaseUsers";
    private DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    List<ListEntity> users = new ArrayList<>();

    boolean user_ref = false;
    private boolean Start = false;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String userID;

    private String user_name;
    private String user_age;
    private String user_info;
    private String user_image;
    private String random_image;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");

    List<ListEntity> events = new ArrayList<>();
    private final String LIST_KEY2 = "BaseEvents";
    private final DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY2);


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AuthorEvent.this.getContentResolver(), filePath);
                binding.userImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void downloadImage(){
        System.out.println(user_image+"\n\n\n\n\n\n\n");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");

        storageRef.child("images/"+user_image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(AuthorEvent.this).load(uri)
                        .into(binding.userImage);
            }
        });
    }


    private void AdapterCall(){
        Adapter adapter = new Adapter(AuthorEvent.this, events);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(AuthorEvent.this));
        binding.recyclerView.setAdapter(adapter);
        System.out.println(events.get(1).getUser_image()+"\n\n\n\n\n\n\n");


        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AuthorEvent.this, EventActivity.class);
                intent.putExtra("event_name", events.get(position).getEvent_name());
                intent.putExtra("event_date", events.get(position).getEvent_date());
                intent.putExtra("event_info", events.get(position).getEvent_info());
                intent.putExtra("event_age", events.get(position).getEvent_age());
                intent.putExtra("videoLink", events.get(position).getVideoLink());
                intent.putExtra("email", events.get(position).getEmail());
                startActivity(intent);
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        email = arguments.getString("email").toString();
        userID = mAuth.getCurrentUser().getUid();
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        ListEntity value = ds2.getValue(ListEntity.class);
                        String valieDB = ds2.getValue(ListEntity.class).getEmail();
                        if (Objects.equals(email, valieDB)) {
                            binding.editName.setText(ds2.getValue(ListEntity.class).getUser_name().toString());
                            binding.editAge.setText(ds2.getValue(ListEntity.class).getUser_age().toString()+" лет");
                            binding.editInfo.setText(ds2.getValue(ListEntity.class).getUser_info().toString());
                            user_image = ds2.getValue(ListEntity.class).getUser_image().toString();
                            userID = ds2.getValue(ListEntity.class).getUserID().toString();
                            System.out.println(user_image + "\n\n\n\n\n\n\n");
                            downloadImage();
                            user_ref = true;

                        }
                    }
                }
                if (!user_ref) {
                    user_image = "standartAva.png";
                    downloadImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AuthorEvent.this, "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });

        eventsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot ds2 : ds.getChildren()){
                        ListEntity value = ds2.getValue(ListEntity.class);
                        String valieDB = ds2.getValue(ListEntity.class).getUserID();
                        if (Objects.equals(userID, valieDB)) {
                            events.add(value);
                        }
                    }
                }
                if (!Start) {
                    System.out.println(events);
                    AdapterCall();
                    Start = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AuthorEvent.this, "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });

        if (Start) {
            AdapterCall();
        }
    }
}