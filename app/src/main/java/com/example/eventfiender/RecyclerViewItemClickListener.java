package com.example.eventfiender;

import android.view.View;
// Интерфейс для того, чтобы сделать кликабельным ресайклер
public interface RecyclerViewItemClickListener {
    void onItemClick(View view, int position);
}
