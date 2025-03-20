package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class Cards_Activity2 extends AppCompatActivity {

    private CardAdapter3 adapter;

    private CardDatabaseHelper3 dbHelper;


    private List<CardItem3> cardList;

    private  int id=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cards2);
        //Log.i("parentTitle",parentTitle);

        dbHelper = new CardDatabaseHelper3(this);



        Button button =findViewById(R.id.add_btn2);
        String parentid = getIntent().getStringExtra("parentid");
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter3(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        cardList=dbHelper.getCardsByParentId(parentid);
        adapter.setCards(cardList);
        button.setOnClickListener(v->{
            showEditContentDialog(this);



        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showEditContentDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("添加新卡片");

        // 使用自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_content2, null);

        // 获取输入框引用
        final TextInputEditText editTextContent = view.findViewById(R.id.editTextContent);
        final TextInputEditText editTextLevel = view.findViewById(R.id.editTextLevel);

        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = editTextContent.getText().toString().trim();
                String newLevelStr = editTextLevel.getText().toString().trim();
                int level ;
                if (!newContent.isEmpty()) {

                    if(newLevelStr.isEmpty()){
                        level=1;
                    }
                    else{
                        level = Integer.parseInt(newLevelStr);
                    }
                    String parentId = getIntent().getStringExtra("parentid");
                    String parenttile=getIntent().getStringExtra("parenttile2");
                    int newId = getNextId(); // 实现获取下一个 ID 的方法
                    CardItem3 newCard = new CardItem3(newContent, parentId, newId, level,parenttile);
                    CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
                    dbHelper.insertCard(newCard);
                    adapter.addCard(newCard);
                } else {
                    Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 示例方法：获取下一个 ID
    private int getNextId() {

        return dbHelper.getMaxId();
    }
}