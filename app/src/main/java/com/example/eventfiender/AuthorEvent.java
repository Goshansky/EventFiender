package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.eventfiender.databinding.ActivityAuthorEventBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthorEvent extends AppCompatActivity {
    private ActivityAuthorEventBinding binding;
    private String email;

    private final String LIST_KEY = "BaseUsers";
    private final DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    // Проверка открытия фрагмента для правильного отображения элементов на экране
    private boolean Start = false;

    private String userID;
    private String user_image;

    List<ListEntity> events = new ArrayList<>();
    private final String LIST_KEY2 = "BaseEvents";
    private final DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY2);

    // Показ изображения пользователя на экране, выгружая его из firebase storage
    public void downloadImage(){
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
        // Вызов адаптера
        Adapter adapter = new Adapter(AuthorEvent.this, events);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(AuthorEvent.this));
        binding.recyclerView.setAdapter(adapter);
        // Кликабельный ресайклер
        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            /**
             * При нажатии на элемент ресайклера открывается новая активность,
             * куда передаются параметры данного события
             * @param view Область ресайклера
             * @param position Позиция нажатого элемента ресайклера
             */
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AuthorEvent.this, EventActivity.class);
                intent.putExtra("event_name", events.get(position).getEvent_name());
                intent.putExtra("event_date", events.get(position).getEvent_date());
                intent.putExtra("event_info", events.get(position).getEvent_info());
                intent.putExtra("event_age", events.get(position).getEvent_age());
                intent.putExtra("videoLink", events.get(position).getVideoLink());
                intent.putExtra("email", events.get(position).getEmail());
                intent.putExtra("stadt", events.get(position).getStadt());
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        email = arguments.getString("email").toString();
        userID = mAuth.getCurrentUser().getUid();
        // Выгрузка данных о пользователе
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String valieDB = ds2.getValue(ListEntity.class).getEmail();
                        if (Objects.equals(email, valieDB)) {
                            binding.editName.setText(ds2.getValue(ListEntity.class).getUser_name().toString());
                            binding.editAge.setText(ds2.getValue(ListEntity.class).getUser_age().toString()+" лет");
                            binding.editInfo.setText(ds2.getValue(ListEntity.class).getUser_info().toString());
                            user_image = ds2.getValue(ListEntity.class).getUser_image().toString();
                            userID = ds2.getValue(ListEntity.class).getUserID().toString();
                            downloadImage();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AuthorEvent.this, "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });
        // Выгрузка данных о событии
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