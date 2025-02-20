package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PatternLockActivity extends AppCompatActivity implements PatternLockView.OnPatternListener{

    private PatternLockView patternView;
    private TextView tvHint;
    private PatternManager patternManager;
    private boolean isSettingMode = false;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pattern_lock);

        patternManager = new PatternManager(this);

        patternView = findViewById(R.id.patternView);
        tvHint = findViewById(R.id.tvHint);
        Button btn_new =findViewById(R.id.btn_new);
        btn_new.setOnClickListener(v->{
            checkPatternStatus();
        });



        patternView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onPatternDetected(String pattern) {
                if (isSettingMode) {
                    handleSetPattern(pattern);
                } else {
                    handleVerifyPattern(pattern);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.PatternLock), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkPatternStatus() {
            isSettingMode = true;
            tvHint.setText("请设置新图案");

    }

    public void onPatternDetected(String pattern) {
        if (isSettingMode) {
            handleSetPattern(pattern);
        } else {
            handleVerifyPattern(pattern);
            tvHint.setText("请输入解锁图案");
        }
    }



    private void handleSetPattern(String pattern) {
        if (pattern.split(",").length < 4) { // 至少4个点
            tvHint.setText("至少连接4个点，请重试");
            patternView.reset();
        } else {
            patternManager.savePattern(pattern);
            tvHint.setText("图案已保存");
            isSettingMode=false;
        }
    }

    private void handleVerifyPattern(String pattern) {
        if (patternManager.verifyPattern(pattern)) {
            startActivity(new Intent(this, LoginSuccessActivity.class));

        } else {
            tvHint.setText("解锁失败，请重试");
            patternView.reset();
        }
    }
}