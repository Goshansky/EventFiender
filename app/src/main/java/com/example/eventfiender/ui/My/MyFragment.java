package com.example.eventfiender.ui.My;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventfiender.Adapter;
import com.example.eventfiender.CreateActivity;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.databinding.FragmentMyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyFragment extends Fragment {

    private FragmentMyBinding binding;
    List<ListEntity> events = new ArrayList<>();
    private String LIST_KEY = "BaseEvents";
    private DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);

    private int NumberStart = 0;

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//    }

    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MyViewModel myViewModel =
                new ViewModelProvider(this).get(MyViewModel.class);

        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



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
                //Toast.makeText(getActivity(), "jkjkjk", Toast.LENGTH_SHORT).show();
                for(DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot ds2 : ds.getChildren()){
                        ListEntity value = ds2.getValue(ListEntity.class);
                        events.add(value);
                    }
                }
                if (NumberStart == 0) {
                    Adapter adapter = new Adapter(getActivity(), events);

                    binding.recyclerView
                            .setLayoutManager(new LinearLayoutManager(getActivity()));
                    binding.recyclerView.setAdapter(adapter);
                    NumberStart+=1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });

        if (NumberStart != 0) {
            Adapter adapter = new Adapter(getActivity(), events);

            binding.recyclerView
                    .setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(adapter);
            NumberStart+=1;
        }

        //final TextView textView = binding.textMy;
        //myViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}