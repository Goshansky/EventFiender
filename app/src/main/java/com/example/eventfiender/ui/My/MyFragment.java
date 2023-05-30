package com.example.eventfiender.ui.My;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventfiender.Adapter;
import com.example.eventfiender.CreateActivity;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.MyEventActivity;
import com.example.eventfiender.RecyclerViewItemClickListener;
import com.example.eventfiender.databinding.FragmentMyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFragment extends Fragment {

    private FragmentMyBinding binding;
    List<ListEntity> events = new ArrayList<>(); // Список параметров событий
    private final String LIST_KEY = "BaseEvents"; // Название базы данных событий
    private final String LIST_KEY2 = "BaseUsers"; // Название базы данных пользователей
    // Подключение к бд
    private final DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    private final DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY2);
    private String userID; // id текущего пользователя
    private String user_name; // Имя текущего пользователя
    private String user_age; // Возраст текущего пользователя
    private String user_info; // Информация о текущем пользователе
    // Проверка открытия фрагмента для правильного отображения элементов на экране
    private boolean Start = false;


    private void AdapterCall(){
        // Вызов адаптера
        Adapter adapter = new Adapter(getActivity(), events);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                Intent intent = new Intent(getActivity(), MyEventActivity.class);
                intent.putExtra("event_name", events.get(position).getEvent_name());
                intent.putExtra("event_date", events.get(position).getEvent_date());
                intent.putExtra("event_info", events.get(position).getEvent_info());
                intent.putExtra("event_age", events.get(position).getEvent_age());
                intent.putExtra("videoLink", events.get(position).getVideoLink());
                intent.putExtra("eventID", events.get(position).getEventID());
                intent.putExtra("stadt", events.get(position).getStadt());
                startActivity(intent);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); // Данные об аунтификации пользователя
        userID = mAuth.getCurrentUser().getUid(); // id пользователя


        binding.fab.setOnClickListener(new View.OnClickListener() {
            /**
             * При нажатие на клавишу, происходит проверка, которая не позволяет пользователю
             * без личных данных перейди во вкладку создания события
             * @param view Область кнопки
             */
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String email = mAuth.getCurrentUser().getEmail();
                usersDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Проходимся по бд и ищем совпадение по почте
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            for (DataSnapshot ds2 : ds.getChildren()) {
                                String valieDB = ds2.getValue(ListEntity.class).getEmail();
                                if (Objects.equals(email, valieDB)) {
                                    user_name = ds2.getValue(ListEntity.class).getUser_name().toString();
                                    user_age = ds2.getValue(ListEntity.class).getUser_age().toString();
                                    user_info = ds2.getValue(ListEntity.class).getUser_info().toString();
                                }
                            }
                        }
                        // Проверка на наличие данных о пользователе
                        if(user_name == null || user_age == null || user_info == null){
                            Toast.makeText(getActivity(), "Введите данные о себе", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getActivity(), CreateActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // Добавляем в список событий только те, которые принадлежат нам
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
                    AdapterCall();
                    Start = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });

        if (Start) {
            AdapterCall();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}