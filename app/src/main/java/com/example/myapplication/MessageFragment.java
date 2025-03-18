package com.example.myapplication;

// MessageFragment.java
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CardAdapter;
import com.example.myapplication.CardItem;
import com.example.myapplication.NewCardActivity;
import com.example.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {



    private static final int PICK_CSV_FILE = 1;
    private int id =1;
    private CardAdapter adapter;
    private CardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // 初始化 RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CardAdapter(getContext(),new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 将 ViewModel 的作用域设置为 Activity，这样在同一个 Activity 内的所有 Fragment 都可以共享同一个 ViewModel，从而保持数据的持久性。
        viewModel = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
        //viewModel = new ViewModelProvider(this).get(CardViewModel.class);
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(getContext());
        List<CardItem> cardList = dbHelper.getCardList();
        viewModel.getCards(cardList).observe(getViewLifecycleOwner(), new Observer<List<CardItem>>() {
            @Override
            public void onChanged(List<CardItem> cards) {
                adapter.setCards(cards);
            }
        });

        // 新建卡片按钮点击事件
        Button buttonAddCard = view.findViewById(R.id.buttonAddCard);
        Button buttonAddCard2 = view.findViewById(R.id.buttonAddCard2);
        Button buttonAddCard3 = view.findViewById(R.id.buttonAddCard3);
        buttonAddCard.setOnClickListener(v -> {
            showNewCardDialog();
        });
        buttonAddCard2.setOnClickListener(v->{
            dbHelper.CardItem_exportToCSV(getContext());
            CardDatabaseHelper2 dbHelper2 = new CardDatabaseHelper2(getContext());
            dbHelper2.CardItem2_exportToCSV(getContext());
            CardDatabaseHelper3 dbHelper3 = new CardDatabaseHelper3(getContext());
            dbHelper3.CardItem3_exportToCSV(getContext());
        });
        buttonAddCard3.setOnClickListener(v->{
            openFilePicker();
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("MesseageFragment", "选中的文件 URI: " + uri.toString());
                // 处理导入的文件
                String filename=getFileName(getContext(),uri);
                String prefix = filename.substring(0, "CardDatabase1".length());
                Log.i("MesseageFragment", "选中的名字: " + prefix);
                Log.i("MesseageFragment", "选中的文件: " + filename);
                if(prefix.equals("CardDatabase1")){
                CardDatabaseHelper dbHelper = new CardDatabaseHelper(getContext());
                dbHelper.importFromCSV(uri,getContext());}
                else if(prefix.equals("CardDatabase2")){
                    CardDatabaseHelper2 dbHelper2 = new CardDatabaseHelper2(getContext());
                    dbHelper2.importFromCSV(uri,getContext());
                }
                else if(prefix.equals("CardDatabase3")){
                    CardDatabaseHelper3 dbHelper3 = new CardDatabaseHelper3(getContext());
                    dbHelper3.importFromCSV(uri,getContext());
                }
                adapter.notifyDataSetChanged();



            }

        }
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

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // 指定 MIME 类型为 CSV 文件
        String[] mimeTypes = {"text/csv", "application/vnd.ms-excel", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // 指定默认的文件类型过滤器（可选）
        intent.setType("*/*");
        // 可选：限制选择单个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        // 可选：设置标题
        intent.putExtra(Intent.EXTRA_TITLE, "选择 CSV 文件");
        // 启动选择器
        startActivityForResult(intent, PICK_CSV_FILE);
    }
    @SuppressLint("Range")
    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
    private void showNewCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("新建卡片");

        // 使用自定义布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_new_card, null);

        // 获取输入框引用
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextLevel = view.findViewById(R.id.editTextLevel);
        RadioGroup radioGroupIcons = view.findViewById(R.id.radioGroupIcons);
        RadioGroup radioGroupIcons2 = view.findViewById(R.id.radioGroupIcons2);
        RadioGroup radioGroupIcons3 = view.findViewById(R.id.radioGroupIcons3);

        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取用户输入
                String title = editTextTitle.getText().toString().trim();
                String levelText = editTextLevel.getText().toString().trim();
                int level = 1; // 默认等级为1
                if (!levelText.isEmpty()) {
                    try {
                        level = Integer.parseInt(levelText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "请输入有效的等级", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // 获取选中的图标
                int selectedIconId = R.mipmap.emj0; // 默认图标
                int selectedRadioButtonId = radioGroupIcons.getCheckedRadioButtonId();
                if (selectedRadioButtonId == R.id.radioIcon1) {
                    selectedIconId = R.mipmap.emj0;
                } else if (selectedRadioButtonId == R.id.radioIcon2) {
                    selectedIconId = R.mipmap.emj1;
                } else if (selectedRadioButtonId == R.id.radioIcon3) {
                    selectedIconId = R.mipmap.emj2;
                } else if (selectedRadioButtonId == R.id.radioIcon4) {
                    selectedIconId = R.mipmap.emj3;
                } else if (selectedRadioButtonId == R.id.radioIcon5) {
                    selectedIconId = R.mipmap.emj4;
                }

                // 处理其他图标组
                int selectedRadioButtonId2 = radioGroupIcons2.getCheckedRadioButtonId();
                if (selectedRadioButtonId2 == R.id.radioIcon6) {
                    selectedIconId = R.mipmap.emj5;
                } else if (selectedRadioButtonId2 == R.id.radioIcon7) {
                    selectedIconId = R.mipmap.emj6;
                } else if (selectedRadioButtonId2 == R.id.radioIcon8) {
                    selectedIconId = R.mipmap.emj7;
                } else if (selectedRadioButtonId2 == R.id.radioIcon9) {
                    selectedIconId = R.mipmap.emj8;
                } else if (selectedRadioButtonId2 == R.id.radioIcon10) {
                    selectedIconId = R.mipmap.emj9;
                }

                int selectedRadioButtonId3 = radioGroupIcons3.getCheckedRadioButtonId();
                if (selectedRadioButtonId3 == R.id.radioIcon11) {
                    selectedIconId = R.mipmap.emj10;
                } else if (selectedRadioButtonId3 == R.id.radioIcon12) {
                    selectedIconId = R.mipmap.emj11;
                } else if (selectedRadioButtonId3 == R.id.radioIcon13) {
                    selectedIconId = R.mipmap.emj12;
                } else if (selectedRadioButtonId3 == R.id.radioIcon14) {
                    selectedIconId = R.mipmap.emj13;
                } else if (selectedRadioButtonId3 == R.id.radioIcon15) {
                    selectedIconId = R.mipmap.emj14;
                }

                // 创建新的卡片


                id=id+1;
                CardItem newCard = new CardItem(id, title, "卡片数", selectedIconId, level);

                // 保存到数据库
                CardDatabaseHelper dbHelper = new CardDatabaseHelper(getContext());
                dbHelper.insertCard(newCard);
                viewModel.addCard(newCard);
                // 更新 UI（假设您有一个 RecyclerView 来显示卡片）
                // 例如：adapter.addCard(newCard);
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