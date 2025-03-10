package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        int parentid = getIntent().getIntExtra("parentid",0);

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
        builder.setTitle("编辑内容");

        // 设置输入框
        final EditText input = new EditText(context);
        input.setText("");
        builder.setView(input);


        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = input.getText().toString().trim();


                if (!newContent.isEmpty()) {
                    int parentid = getIntent().getIntExtra("parentid",0);
                    id=id+1;
                    CardItem3 newCard = new CardItem3(newContent,parentid,id);
                    dbHelper = new CardDatabaseHelper3(context);
                    dbHelper.insertCard(newCard);
                    adapter.addCard(newCard);

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
}