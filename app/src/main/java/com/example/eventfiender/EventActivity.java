package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.eventfiender.databinding.ActivityEventBinding;

public class EventActivity extends AppCompatActivity {
    private String email;
    @SuppressLint("SetTextI18n") // Чтобы не подчеркивал 35-38 строчки
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.eventfiender.databinding.ActivityEventBinding binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();
        String eventName = arguments.getString("event_name");
        String eventDate = arguments.getString("event_date");
        String eventAge = arguments.getString("event_age");
        String eventInfo = arguments.getString("event_info");
        String myVideoYoutubeId = arguments.getString("videoLink").toString();
        email = arguments.getString("email").toString();
        String stadt = arguments.getString("stadt").toString();

        binding.eventName.setText(eventName);
        binding.eventDate.setText("Дата события: "+ eventDate);
        binding.eventAge.setText("Возрастные ограничения: "+ eventAge +"+");
        binding.eventInfo.setText("Информация о событии:\n"+ eventInfo);
        binding.textStadt.setText("Город: "+ stadt);

        // Работа с видео-фрагментом с ютуба
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView youtubeWebView;
        youtubeWebView = binding.youtubeWebView;
        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        youtubeWebView.loadUrl("https://www.youtube.com/embed/" + myVideoYoutubeId);
        binding.event.setText("Cсылка на видео: \nhttps://youtu.be/" + myVideoYoutubeId);
        // Обработчик кнопки "Автор"
        binding.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, AuthorEvent.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Вернуться назад
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}