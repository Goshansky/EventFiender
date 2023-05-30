package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.eventfiender.databinding.ActivityEventBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventActivity extends AppCompatActivity {
    private ActivityEventBinding binding;
    private String eventName, eventDate, eventAge, eventInfo, myVideoYoutubeId, stadt, eventImage, eventID, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();
        eventName = arguments.getString("event_name");
        eventDate = arguments.getString("event_date");
        eventAge = arguments.getString("event_age");
        eventInfo = arguments.getString("event_info");
        myVideoYoutubeId = arguments.getString("videoLink").toString();
        email = arguments.getString("email").toString();
        stadt = arguments.getString("stadt").toString();

        binding.eventName.setText(eventName);
        binding.eventDate.setText("Дата события: "+eventDate);
        binding.eventAge.setText("Возрастные ограничения: "+eventAge+"+");
        binding.eventInfo.setText("Информация о событии:\n"+eventInfo);
        binding.textStadt.setText("Город: "+stadt);

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
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}