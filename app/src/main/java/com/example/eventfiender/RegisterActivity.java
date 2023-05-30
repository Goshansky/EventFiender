package com.example.eventfiender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_register;
    private EditText password_register;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();


        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        Button btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверка на корректность введённых данных
                if (email_register.getText().toString().isEmpty() || password_register.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Пустые поля", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(email_register.getText().toString().trim(), password_register.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email_register.getText().toString());
                                        ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("password").setValue(password_register.getText().toString());

                                        Intent intent = new Intent (RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Некорректный ввод", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}