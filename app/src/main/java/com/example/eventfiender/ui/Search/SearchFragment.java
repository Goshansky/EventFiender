package com.example.eventfiender.ui.Search;

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
import com.example.eventfiender.EventActivity;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.RecyclerViewItemClickListener;
import com.example.eventfiender.databinding.FragmentSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    List<ListEntity> events = new ArrayList<>(); // Список параметров событий
    private final String LIST_KEY = "BaseEvents"; // Название базы данных событий
    // Подключение к бд
    private final DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
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
                Intent intent = new Intent(getActivity(), EventActivity.class);
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Записываем все события для последующего их вывода на экран
        eventsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Отчистка поля, чтобы элементы не дублировались
                events.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot ds2 : ds.getChildren()){
                        ListEntity value = ds2.getValue(ListEntity.class);
                        // создание уникального id события
                        eventsDB.child(ds.getKey()).child("0").child("eventID").setValue(ds.getKey());
                        events.add(value); // Добавляем событие
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