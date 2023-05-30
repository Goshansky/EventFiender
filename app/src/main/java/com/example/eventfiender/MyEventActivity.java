package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.eventfiender.databinding.ActivityMyEventBinding;
import com.google.firebase.database.FirebaseDatabase;

public class MyEventActivity extends AppCompatActivity {

    private ActivityMyEventBinding binding;
    private String eventName, eventDate, eventAge, eventInfo, myVideoYoutubeId, eventID, stadt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();
        eventName = arguments.getString("event_name");
        eventDate = arguments.getString("event_date");
        eventAge = arguments.getString("event_age");
        eventInfo = arguments.getString("event_info");
        myVideoYoutubeId = arguments.getString("videoLink").toString();
        eventID = arguments.getString("eventID").toString();
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_edit:
                this.finish();
                Intent intent = new Intent(MyEventActivity.this, EditEventActivity.class);
                intent.putExtra("eventID", eventID);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.action_delete:
                FirebaseDatabase.getInstance()
                        .getReference("BaseEvents")
                        .child(eventID).removeValue();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}