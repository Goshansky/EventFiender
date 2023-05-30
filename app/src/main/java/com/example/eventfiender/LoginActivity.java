package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText password_login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        Button btn_login = findViewById(R.id.btn_login);
        TextView register_txt = findViewById(R.id.register_txt);
        mAuth = FirebaseAuth.getInstance();
        // Создать аккаунт
        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверка на корректность введённых данных
                if (email_login.getText().toString().isEmpty() || password_login.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email_login.getText().toString().trim(), password_login.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}