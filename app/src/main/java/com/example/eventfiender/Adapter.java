package com.example.eventfiender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventfiender.databinding.ListItemBinding;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    private final List<ListEntity> data; // Данные для вывода в список
    private final LayoutInflater localInflater; // "Раздуватель" с контекстом
    private RecyclerViewItemClickListener clickListener; // Кликабельность

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
    @SuppressLint("SetTextI18n") // Чтобы не подчеркивал 49 и 50 строчки
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        ListEntity item = data.get(position);
        holder.event_name.setText(item.getEvent_name());
        holder.event_date.setText("Дата: "+item.getEvent_date());
        holder.event_age.setText("Возраст: "+item.getEvent_age()+"+");

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

        public ViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            event_name = binding.eventName;
            event_date = binding.eventDate;
            event_age = binding.eventAge;
        }
    }
}
