package com.example.eventfiender.ui.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventfiender.Adapter;
import com.example.eventfiender.EventActivity;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.RecyclerViewItemClickListener;
import com.example.eventfiender.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    List<ListEntity> events = new ArrayList<>();
    private String LIST_KEY = "BaseEvents";
    private DatabaseReference eventsDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    private boolean Start = false;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String email;
    private String userID;

    private void AdapterCall(){
        Adapter adapter = new Adapter(getActivity(), events);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DataSnapshot ds;
                //Toast.makeText(getActivity(), events.get(position).getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra("event_name", events.get(position).getEvent_name());
                intent.putExtra("event_date", events.get(position).getEvent_date());
                intent.putExtra("event_info", events.get(position).getEvent_info());
                intent.putExtra("event_age", events.get(position).getEvent_age());
                intent.putExtra("videoLink", events.get(position).getVideoLink());
                startActivity(intent);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        //email = mAuth.getCurrentUser().getEmail();
        //userID = mAuth.getCurrentUser().getUid();

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


        //final TextView textView = binding.textSearch;
        //searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}