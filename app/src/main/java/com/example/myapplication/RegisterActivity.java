package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);



        Intent intent0 =getIntent();
        intent0.putExtra("value",2);
        setResult(RESULT_OK,intent0);


        EditText editTextUsername=findViewById(R.id.editTextUsername);
        EditText editTextPassword=findViewById(R.id.editTextPassword);
        Button button_register=findViewById(R.id.register);

        button_register.setOnClickListener(v->{
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

// 检查输入是否为空
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            } else {
                boolean isRegistered = dbHelper.registerUser(username, password);
                if (isRegistered) {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    
}