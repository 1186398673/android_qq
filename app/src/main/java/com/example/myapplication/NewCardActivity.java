package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewCardActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private RadioGroup radioGroupIcons;
    private Button buttonSave;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_card);
        // 初始化视图
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        radioGroupIcons = findViewById(R.id.radioGroupIcons);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v->{
            // 获取用户输入
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();
            int id = 0;

            // 获取选中的图标
            int selectedIconId = R.mipmap.user; // 默认图标
            int selectedRadioButtonId = radioGroupIcons.getCheckedRadioButtonId();
            if (selectedRadioButtonId == R.id.radioIcon1) {
                selectedIconId = R.mipmap.user;
            } else if (selectedRadioButtonId == R.id.radioIcon2) {
                selectedIconId = R.mipmap.user;
            } else if (selectedRadioButtonId == R.id.radioIcon3) {
                selectedIconId = R.mipmap.user;
            }
            id=id++;

            // 创建新的卡片
            CardItem newCard = new CardItem(id,title, content, selectedIconId);







            // 返回数据到 MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newCard",  newCard);
            setResult(RESULT_OK, resultIntent);
            finish();
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_card), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}