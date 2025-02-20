package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // 绑定控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        ImageButton btn_other = findViewById(R.id.btn_other);
        ImageButton btn_register = findViewById(R.id.btn_register);
        ImageButton btn_more = findViewById(R.id.btn_more);
        ImageButton btn_phone = findViewById(R.id.btn_phone);
        checkBox = findViewById(R.id.checkbox);

        // 设置选中状态监听器
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("CheckBox", "状态变为选中");
                } else {
                    Log.d("CheckBox", "状态变为未选中");
                }
            }
        });

        btn_register.setOnClickListener(v->
        {

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            //startActivity(intent);
           startActivityFromChild(MainActivity.this,intent,1);


        });
        btn_other.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, PatternLockActivity.class);
            startActivity(intent);
        });

        btn_more.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.baidu.com"));
            startActivity(intent);
        });

        btn_phone.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
            // 创建Intent对象


        });








        btnLogin.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    // 使用数据库助手进行验证
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    if (dbHelper.validateUser(username, password)) {
                        ImageView imageView=findViewById(R.id.qq);
                        imageView.setImageResource(R.mipmap.qq_img);
                        Intent intent = new Intent(MainActivity.this, LoginSuccessActivity.class);
                        intent.putExtra("USERNAME", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请勾选协议", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






    }
    @Override
    protected void  onActivityResult(int requestcode,int resultcode,Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode==1){
            if(resultcode== RESULT_OK){
                int value=data.getIntExtra("value",0);
                Log.i("onActivityResul",value+"");
            }

        }
    }
}