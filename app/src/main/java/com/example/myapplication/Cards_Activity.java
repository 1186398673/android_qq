package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Cards_Activity extends AppCompatActivity {


    private CardAdapter2 adapter;

    private CardDatabaseHelper2 dbHelper;


    private List<CardItem2> cardList;


    private String parentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cards);



        String parentTitle = getIntent().getStringExtra("parentTitle");
        //Log.i("parentTitle",parentTitle);

        dbHelper = new CardDatabaseHelper2(this);



        Button button =findViewById(R.id.add_btn);

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter2(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        cardList = dbHelper.getCardsByParentTitle(parentTitle);
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

                    CardItem2 newCard = new CardItem2(newContent,parentTitle);
                    adapter.addCard(newCard);
                    dbHelper = new CardDatabaseHelper2(context);
                    dbHelper.insertCard(newCard);

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