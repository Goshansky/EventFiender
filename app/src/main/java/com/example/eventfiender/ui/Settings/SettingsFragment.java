package com.example.eventfiender.ui.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.eventfiender.ListEntity;
import com.example.eventfiender.LoginActivity;
import com.example.eventfiender.databinding.FragmentSettingsBinding;
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

public class SettingsFragment extends Fragment {

    private Uri filePath; // Выбранное фото и файла
    private final int PICK_IMAGE_REQUEST = 71; // Код запроса для изображения
    private FragmentSettingsBinding binding;
    private final String LIST_KEY = "BaseUsers";
    private DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference(LIST_KEY);
    List<ListEntity> users = new ArrayList<>(); // Список пользователой
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance(); // Для работы с изображениями в firebase
    // Данные о пользователе
    private String email;
    private String userID;
    private String user_name;
    private String user_age;
    private String user_info;
    private String user_image;
    // Переменная, отвечающая заданные о пользователе.
    // Если он не вводил никаких данных, то у него автоматически ставится стандартная аватарка.
    // Если данные были введены, то показываем их на экране.
    boolean user_ref = false;

    // Выбор изображения
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    // Загрузка изображения в firebase storage а также прогресс бар загрузки
    private void uploadImage(){
        // Если мы выбрали фотографию
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String random_image = UUID.randomUUID().toString(); // Уникальный идентификатор изображения
            user_image = random_image;
            StorageReference ref = storage.getReference().child("images/"+ random_image);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Загружено", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Ошибка "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Загрузка "+(int)progress+"%");
                        }
                    });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // В этом проверяем, будет ли код запроса равен PICK_IMAGE_REQUEST,
        // с результатом, равным RESULT_OK, и доступными данными.
        // Если все это верно, то отобразить выбранное изображение в ImageView.
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

    // Показ изображения пользователя на экране, выгружая его из firebase storage
    public void downloadImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");

        storageRef.child("images/"+user_image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(getActivity()).load(uri)
                        .into(binding.userImage);
            }
        });
        // При нажатии на фото, можно его поменять
        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        userID = mAuth.getCurrentUser().getUid();


        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String valieDB = ds2.getValue(ListEntity.class).getEmail();
                        // Если данные о таком пользователе есть, то выгружаем его данные на экран
                        if (Objects.equals(email, valieDB)) {
                            binding.editName.setText(ds2.getValue(ListEntity.class).getUser_name().toString());
                            binding.editAge.setText(ds2.getValue(ListEntity.class).getUser_age().toString());
                            binding.editInfo.setText(ds2.getValue(ListEntity.class).getUser_info().toString());
                            user_image = ds2.getValue(ListEntity.class).getUser_image().toString();
                            downloadImage();
                            user_ref = true;

                        }
                    }
                }
                if (!user_ref) {
                    // Иначе ставим стандартную фотку
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
                if (user_image == null) user_image = "standartAva.png";
                user_name = String.valueOf(binding.editName.getText());
                user_age = String.valueOf(binding.editAge.getText());
                user_info = String.valueOf(binding.editInfo.getText());

                // Если пользователь ввел данные о себе
                if (!user_info.isEmpty() & !user_age.isEmpty() & !user_name.isEmpty()) {

                    mAuth = FirebaseAuth.getInstance();
                    email = mAuth.getCurrentUser().getEmail();
                    userID = mAuth.getCurrentUser().getUid();

                    uploadImage();
                    filePath = null;

                    // Если пользователь новый, то заносим его в бд
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
                    // Иначе изменяем данные о нём
                    else {

                        usersDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    for (DataSnapshot ds2 : ds.getChildren()) {
                                        String valieDB = ds2.getValue(ListEntity.class).getEmail();
                                        if (Objects.equals(email, valieDB)) {
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
                else {
                    Toast.makeText(getActivity(), "Вы не ввели данные о себе", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Обработчик кнопки для выхода из аккаунта
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Context context = getContext();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}