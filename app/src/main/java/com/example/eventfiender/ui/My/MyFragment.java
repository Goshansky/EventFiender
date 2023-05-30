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
    List<ListEntity> events = new ArrayList<>();
    private final String LIST_KEY = "BaseEvents";
    private final DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);

    private String user_name = "";
    private String userID;

    private boolean Start = false;


    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String email;


    private void AdapterCall(){
        Adapter adapter = new Adapter(getActivity(), events);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DataSnapshot ds;
                Intent intent = new Intent(getActivity(), MyEventActivity.class);
                intent.putExtra("event_name", events.get(position).getEvent_name());
                intent.putExtra("event_date", events.get(position).getEvent_date());
                intent.putExtra("event_info", events.get(position).getEvent_info());
                intent.putExtra("event_age", events.get(position).getEvent_age());
                intent.putExtra("videoLink", events.get(position).getVideoLink());
                intent.putExtra("eventID", events.get(position).getEventID());
                intent.putExtra("stadt", events.get(position).getStadt());
                intent.putExtra("event_image", events.get(position).getEvent_image());
                startActivity(intent);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        userID = mAuth.getCurrentUser().getUid();


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateActivity.class);
                startActivity(intent);
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