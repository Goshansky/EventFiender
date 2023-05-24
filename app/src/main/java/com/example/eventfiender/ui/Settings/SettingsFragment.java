package com.example.eventfiender.ui.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.signin.internal.Storage;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class SettingsFragment extends Fragment {
    private final static String KEY = "key";

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;


    private FragmentSettingsBinding binding;
    private final String LIST_KEY = "BaseUsers";
    private DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    List<ListEntity> users = new ArrayList<>();

    boolean user_ref = false;

    //private Button logout_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String email;
    private String userID;

    private String user_name;
    private String user_age;
    private String user_info;
    private String user_image;
    private String random_image;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");



    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void uploadImage(){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            random_image = UUID.randomUUID().toString();
            user_image = random_image;
            StorageReference ref = storage.getReference().child("images/"+ random_image);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
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

                Picasso.with(getActivity()).load(uri)
                        .into(binding.userImage);
            }
        });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        userID = mAuth.getCurrentUser().getUid();


        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(getActivity(), "jkjkjk", Toast.LENGTH_SHORT).show();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        ListEntity value = ds2.getValue(ListEntity.class);
                        String valieDB = ds2.getValue(ListEntity.class).getEmail();
                        //Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();
                        if (Objects.equals(email, valieDB)) {
                            //Toast.makeText(getActivity(), "jkjkjk", Toast.LENGTH_SHORT).show();
                            binding.editName.setText(ds2.getValue(ListEntity.class).getUser_name().toString());
                            binding.editAge.setText(ds2.getValue(ListEntity.class).getUser_age().toString());
                            binding.editInfo.setText(ds2.getValue(ListEntity.class).getUser_info().toString());
                            user_image = ds2.getValue(ListEntity.class).getUser_image().toString();
                            System.out.println(user_image + "\n\n\n\n\n\n\n");
                            //if (user_image == null) user_image = "standartAva.png";
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
                Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
            }
        });


        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(user_image+"\n\n\n\n\n\n");
                if (user_image == null) user_image = "standartAva.png";
                //System.out.println(user_image+"\n\n\n\n\n\n");
                user_name = String.valueOf(binding.editName.getText());
                user_age = String.valueOf(binding.editAge.getText());
                user_info = String.valueOf(binding.editInfo.getText());

                mAuth = FirebaseAuth.getInstance();
                email = mAuth.getCurrentUser().getEmail();
                userID = mAuth.getCurrentUser().getUid();

                //Toast.makeText(getActivity(), random_image.toString(), Toast.LENGTH_SHORT).show();
                uploadImage();
                filePath = null;

                if (!user_ref) {
                    usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
                    users.add(new ListEntity(
                            user_name,
                            user_age,
                            user_info,
                            userID,
                            email,
                            user_image
                    ));
                    usersDB.push().setValue(users);
                }
                else{

                    usersDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Toast.makeText(getActivity(), "jkjkjk", Toast.LENGTH_SHORT).show();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                for (DataSnapshot ds2 : ds.getChildren()){
                                    ListEntity value = ds2.getValue(ListEntity.class);
                                    String valieDB = ds2.getValue(ListEntity.class).getEmail();
                                    //Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();
                                    if (Objects.equals(email, valieDB)) {
                                        //Toast.makeText(getActivity(), ds.getKey(), Toast.LENGTH_SHORT).show();
                                        usersDB.child(ds.getKey()).child("0").child("user_name").setValue(user_name);
                                        usersDB.child(ds.getKey()).child("0").child("user_age").setValue(user_age);
                                        usersDB.child(ds.getKey()).child("0").child("user_info").setValue(user_info);
                                        usersDB.child(ds.getKey()).child("0").child("user_image").setValue(user_image);
                                        user_ref = true;

                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Ошибка чтения БД", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

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