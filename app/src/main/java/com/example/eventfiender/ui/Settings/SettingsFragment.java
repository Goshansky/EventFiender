package com.example.eventfiender.ui.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventfiender.CreateActivity;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.LoginActivity;
import com.example.eventfiender.MainActivity;
import com.example.eventfiender.R;
import com.example.eventfiender.databinding.ActivityCreateBinding;
import com.example.eventfiender.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class SettingsFragment extends Fragment {
    private final static String KEY = "key";


    private FragmentSettingsBinding binding;
    private String user_name;
    private final String LIST_KEY = "BaseUsers";
    private DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    List<ListEntity> users = new ArrayList<>();
    private boolean SaveUser = false;

    //private Button logout_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String email;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



//        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pref.getString(KEY, "мое имя");
//                user_name = binding.editTextName.getText().toString();
//                saveData(user_name);
//                System.out.println(usersDB+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//
//                usersDB.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        SaveUser = false;
//                        user_name = pref.getString(KEY, "мое имя");
//                        //Toast.makeText(getActivity(), user_name, Toast.LENGTH_SHORT).show();
//                        for(DataSnapshot ds : snapshot.getChildren()){
//                            for (DataSnapshot ds2 : ds.getChildren()){
//                            if (user_name == ds2.getValue(ListEntity.class).getUser_name()){
//                                    SaveUser = true;
//                                }
//                                Toast.makeText(getActivity(), user_name+"+"+pref.getString(KEY, "мое имя")+"+"+ds2.getValue(ListEntity.class).getUser_name(), Toast.LENGTH_SHORT).show();
//
//                                //users.add(value);
//                            }
//                        }
//                        if (!SaveUser) {
//                            users.add(new ListEntity(
//                                    user_name,
//                                    userID
//                            ));
//                            usersDB.push().setValue(users);
//                            usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//        });

        mAuth = FirebaseAuth.getInstance();
        System.out.println(mAuth.getCurrentUser().getUid());
        System.out.println(mAuth.getCurrentUser().getEmail());

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Context context = getContext();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);

                //Intent intent = new Intent (SettingsFragment.this, LoginActivity.class);
                //startActivity(intent);
            }
        });

        return root;
    }
//    private void saveData(String user_name){
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(KEY, user_name);
//        editor.apply();
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}