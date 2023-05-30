package com.example.eventfiender;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventfiender.databinding.ListItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    private final List<ListEntity> data; // данные для вывода в список
    private final LayoutInflater localInflater; // "раздуватель" с контекстом
    private RecyclerViewItemClickListener clickListener;
    private Uri filePath;
    private String temp_image;



    public void setOnItemClickListener(RecyclerViewItemClickListener listener) {
        clickListener = listener;
    }

    public Adapter(Context context, List<ListEntity> data) {
        this.data = data;
        this.localInflater = LayoutInflater.from(context);
    }


    // Создание вьюхолдера из разметки
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ListItemBinding binding = ListItemBinding.inflate(localInflater, parent, false);
        return new ViewHolder(binding);
    }

    // Выставляет значения из списка данных во вьюхи по номеру элемента списка
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        ListEntity item = data.get(position);
        holder.event_name.setText(item.getEvent_name());
        holder.event_date.setText("Дата: "+item.getEvent_date());
        holder.event_age.setText("Возраст: "+item.getEvent_age()+"+");

        temp_image = item.getEvent_image();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://eventfiender-d375f.appspot.com");

        storageRef.child("images/"+temp_image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(localInflater.getContext()).load(uri)
                        .into(holder.event_image);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(view, holder.getAdapterPosition());
                }
            }
        });
    }


    // Возвращает размер списка данных, нужно для внутренней работы ресайклера
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Хранит переменные вьюх в разметке элементов списка
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView event_name;
        TextView event_date;
        TextView event_age;
        ImageView event_image;

        public ViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            event_name = binding.eventName;
            event_date = binding.eventDate;
            event_age = binding.eventAge;
            event_image = binding.eventImage;
        }
    }
}
