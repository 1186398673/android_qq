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
    private RadioGroup radioGroupIcons2;

    private RadioGroup radioGroupIcons3;
    private Button buttonSave;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_card);
        // 初始化视图
        editTextTitle = findViewById(R.id.editTextTitle);
        radioGroupIcons = findViewById(R.id.radioGroupIcons);
        radioGroupIcons2 = findViewById(R.id.radioGroupIcons2);
        radioGroupIcons3 = findViewById(R.id.radioGroupIcons3);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v->{
            // 获取用户输入
            String title = editTextTitle.getText().toString();
            String content = "卡片数";
            int id = 0;

            // 获取选中的图标
            int selectedIconId = R.mipmap.emj0; // 默认图标
            int selectedRadioButtonId = radioGroupIcons.getCheckedRadioButtonId();
            int selectedRadioButtonId2 = radioGroupIcons2.getCheckedRadioButtonId();
            int selectedRadioButtonId3 = radioGroupIcons3.getCheckedRadioButtonId();
            if (selectedRadioButtonId == R.id.radioIcon1) {
                selectedIconId = R.mipmap.emj0;
            } else if (selectedRadioButtonId == R.id.radioIcon2) {
                selectedIconId = R.mipmap.emj1;
            } else if (selectedRadioButtonId == R.id.radioIcon3) {
                selectedIconId = R.mipmap.emj2;
            }
            else if (selectedRadioButtonId == R.id.radioIcon4){
                selectedIconId = R.mipmap.emj3;
            }
            else if (selectedRadioButtonId == R.id.radioIcon5){
                selectedIconId = R.mipmap.emj4;
            }

            if(selectedRadioButtonId2==R.id.radioIcon6){
                selectedIconId = R.mipmap.emj5;
            }
            else if (selectedRadioButtonId2 == R.id.radioIcon7){
                selectedIconId = R.mipmap.emj6;
            }
            else if (selectedRadioButtonId2 == R.id.radioIcon8){
                selectedIconId = R.mipmap.emj7;
            }
            else if (selectedRadioButtonId2 == R.id.radioIcon9){
                selectedIconId = R.mipmap.emj8;
            }
            else if (selectedRadioButtonId2 == R.id.radioIcon10){
                selectedIconId = R.mipmap.emj9;
            }

            if(selectedRadioButtonId3==R.id.radioIcon11){
                selectedIconId = R.mipmap.emj10;
            }
            else if (selectedRadioButtonId3 == R.id.radioIcon12){
                selectedIconId = R.mipmap.emj11;
            }
            else if (selectedRadioButtonId3 == R.id.radioIcon13){
                selectedIconId = R.mipmap.emj12;
            }
            else if (selectedRadioButtonId3 == R.id.radioIcon14){
                selectedIconId = R.mipmap.emj13;
            }
            else if (selectedRadioButtonId3 == R.id.radioIcon15){
                selectedIconId = R.mipmap.emj14;
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