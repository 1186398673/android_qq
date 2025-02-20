package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class LoginSuccessActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment;

    private CardAdapter adapter;



    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_success);



       // 初始化底部导航栏
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        bottomNav.setSelectedItemId(R.id.nav_message);
        selectedFragment = new MessageFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();





        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
           if(item.getItemId()==R.id.nav_message){
                    selectedFragment = new MessageFragment();
           }
           if(item.getItemId()==R.id.nav_channel)
                    selectedFragment = new ChannelFragment();


            if(item.getItemId()==R.id.nav_video)
                    selectedFragment = new VideoFragment();
            if(item.getItemId()== R.id.nav_contacts)
                    selectedFragment = new ContactsFragment();
            if(item.getItemId()== R.id.nav_dynamic)
                    selectedFragment = new DynamicFragment();
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });







        // 接收传递的数据
        String username = getIntent().getStringExtra("USERNAME");
        TextView tvWelcome = findViewById(R.id.tv_welcome);


        // 显示欢迎信息
        tvWelcome.setText(username + "，登录成功！");







        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LoginSuccess), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }





}