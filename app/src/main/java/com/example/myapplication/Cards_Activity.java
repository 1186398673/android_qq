package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class Cards_Activity extends AppCompatActivity {


    private CardAdapter2 adapter;

    private CardDatabaseHelper2 dbHelper;


    private List<CardItem2> cardList;

    private  int id=1;




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

        // 使用自定义布局
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_content, null);

        // 获取输入框引用
        final TextInputEditText editTextContent = view.findViewById(R.id.editTextContent);
        final TextInputEditText editTextDefine = view.findViewById(R.id.editTextDefine);
        final TextInputEditText editTextMeaning = view.findViewById(R.id.editTextMeaning);
        final TextInputEditText editTextRange = view.findViewById(R.id.editTextRange);
        final TextInputEditText editTextExample = view.findViewById(R.id.editTextExample);
        final TextInputEditText editTextLevel = view.findViewById(R.id.editTextLevel);
        // 设置输入框的提示文本（可选）
        editTextContent.setHint("请输入标题");
        editTextDefine.setHint("请输入定义");
        editTextMeaning.setHint("请输入意义");
        editTextRange.setHint("请输入适用范围");
        editTextExample.setHint("请输入例子");
        editTextLevel.setHint("请输入等级");

        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = editTextContent.getText().toString().trim();
                String newDefine = editTextDefine.getText().toString().trim();
                String newMeaning = editTextMeaning.getText().toString().trim();
                String newRange = editTextRange.getText().toString().trim();
                String newExample = editTextExample.getText().toString().trim();
                String newLevel = editTextLevel.getText().toString().trim();
                int level = Integer.parseInt(newLevel);

                if (!newContent.isEmpty()) {
                    String parentTitle = getIntent().getStringExtra("parentTitle");
                    id = cardList.size() + 1;
                    CardItem2 newCard = new CardItem2(newContent, newDefine, newMeaning, newRange, newExample, parentTitle, id,level);
                    dbHelper.insertCard(newCard);
                    adapter.addCard(newCard);
                } else {
                    // 显示错误提示
                    TextInputLayout textInputLayoutContent = view.findViewById(R.id.textInputLayoutContent);
                    textInputLayoutContent.setError("内容不能为空");
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

        AlertDialog dialog = builder.create();

        // 监听对话框显示事件，设置焦点和软键盘
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                editTextContent.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextContent, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.show();
    }
}