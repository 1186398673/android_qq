package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;





public class Cards_Activity2 extends AppCompatActivity implements CardAdapter3.OnItemClickListener {

    private CardAdapter3 adapter;

    private CardDatabaseHelper3 dbHelper;


    private List<CardItem3> cardList;

    private static final int REQUEST_CODE_SELECT_IMAGE=3;

    private static final int REQUEST_CODE_SELECT_IMAGE2=4;

    private String selectImage;



    private Uri selectImageuri;

    public static final String DEFAULT_IMAGE_URL = "";






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
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter3(this, new ArrayList<>(),this);
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

        // 获取图片视图和按钮引用

        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);


        selectImage="";
        // 设置选择图片按钮的点击事件
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent来选择图片
                openFilePicker();
                // 启动选择图片的活动，并等待结果

            }
        });

        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = editTextContent.getText().toString().trim();
                String newLevelStr = editTextLevel.getText().toString().trim();
                int level;
                    if(newLevelStr.isEmpty()){
                        level = 1;
                    }
                    else{
                        level = Integer.parseInt(newLevelStr);
                    }
                    String parentId = getIntent().getStringExtra("parentid");
                    String parentTile = getIntent().getStringExtra("parenttile2");
                    int newId = getNextId(); // 实现获取下一个 ID 的方法

                    if(selectImage.isEmpty())
                    {
                        CardItem3 newCard = new CardItem3(newContent, parentId, newId, level, parentTile,DEFAULT_IMAGE_URL);
                        CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
                        dbHelper.insertCard(newCard);
                        adapter.addCard(newCard);
                    }
                    else {

                        Log.i("VideoFragment2", "选中的文件 URI: " + selectImageuri.toString());
                        String SelectImageuri=selectImageuri.toString();
                        CardItem3 newCard = new CardItem3(newContent, parentId, newId, level, parentTile,SelectImageuri);
                        CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
                        dbHelper.insertCard(newCard);
                        adapter.addCard(newCard);
                        selectImageuri=null;
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("VideoFragment", "选中的文件 URI: " + uri.toString());
                // 处理导入的文件
                selectImageuri=dbHelper.saveImageToExternalStorage(uri,this);
                selectImage=  uri.toString();

            }
        }
        else {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("VideoFragment", "选中的文件 URI: " + uri.toString());
                // 处理导入的文件
                selectImageuri=dbHelper.saveImageToExternalStorage(uri,this);
                selectImage=  uri.toString();

            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // 使用 ACTION_OPEN_DOCUMENT 更加通用
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // 设置为所有图片类型

        // 可选：指定多个 MIME 类型
        String[] mimeTypes = {"image/png", "image/jpeg", "image/gif", "image/bmp", "image/webp"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // 可选：限制选择单个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        // 启动选择器，并等待结果
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);

    }
    private void openFilePicker2() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // 使用 ACTION_OPEN_DOCUMENT 更加通用
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // 设置为所有图片类型

        // 可选：指定多个 MIME 类型
        String[] mimeTypes = {"image/png", "image/jpeg", "image/gif", "image/bmp", "image/webp"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // 可选：限制选择单个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        // 启动选择器，并等待结果
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE2);

    }

    public void showEditContentDialog2(final Context context,final CardItem3 card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改卡片");

        // 使用自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_content2, null);

        // 获取输入框引用
        final TextInputEditText editTextContent = view.findViewById(R.id.editTextContent);
        final TextInputEditText editTextLevel = view.findViewById(R.id.editTextLevel);

        // 获取图片视图和按钮引用

        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);


        selectImage="";
        // 设置选择图片按钮的点击事件
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent来选择图片
                openFilePicker2();
                // 启动选择图片的活动，并等待结果

            }
        });

        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = editTextContent.getText().toString().trim();
                String newLevelStr = editTextLevel.getText().toString().trim();
                int level;
                if(newLevelStr.isEmpty()){
                    level = 1;
                }
                else{
                    level = Integer.parseInt(newLevelStr);
                }

                if(selectImage.isEmpty())
                {
                    card.setContent(newContent);
                    card.setLevel(level);
                    card.setImageUrl("");
                    dbHelper.updateCard(card);
                    adapter.notifyDataSetChanged();
                }
                else {
                    card.setContent(newContent);
                    card.setLevel(level);
                    card.setImageUrl(selectImageuri.toString());
                    dbHelper.updateCard(card);
                    adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(CardItem3 card) {
        showEditContentDialog2(this,card);
    }
}