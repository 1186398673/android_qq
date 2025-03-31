package com.example.myapplication;







import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CardItem2;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.util.List;

public class CardAdapter3 extends RecyclerView.Adapter<CardAdapter3.CardViewHolder3> {

    private List<CardItem3> cardList;//用于存储要显示的卡片数据
    private Context context;







    // 另一种构造函数（如果需要）
    public CardAdapter3(Context context, List<CardItem3> cardList) {
        this.cardList = cardList;
        this.context=context;



    }

    // 创建 ViewHolder,加载布局文件
    @NonNull
    @Override
    public CardViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list2_card, parent, false);

        return new CardViewHolder3(view);
    }

    // 绑定数据到 ViewHolder，为子项赋值，通过position获取实例，设置卡片行为
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder3 holder, int position) {
        CardItem3 card = cardList.get(position);

        if(card.getImageUrl().equals(""))
        {
            holder.imageView.setImageResource(R.mipmap.emj10);
        }
        else {
            Uri imageUri = Uri.parse(card.getImageUrl());
            holder.imageView.setImageURI(imageUri);
        }
        holder.content.setText(card.getContent());
        holder.level.setText("等级"+String.valueOf(card.getLevel()));
        holder.content.setOnClickListener(v->{
            CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
            showEditContentDialog(context,card.getid(),card.getContent(),String.valueOf(card.getLevel()),card,dbHelper,position);
        });
        holder.content.setOnLongClickListener(v->{
            removeCard(position);
            CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
            dbHelper.deleteCardById(card.getid());
            return true;
        });









    }

    // 返回列表大小
    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    // ViewHolder类,每个控件在item中的具体位置
    static class CardViewHolder3 extends RecyclerView.ViewHolder {
        TextView content,level;

        ImageView imageView;
        public CardViewHolder3(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.cardContent2);
            level = itemView.findViewById(R.id.cardContent3);
            imageView=itemView.findViewById(R.id.imageView_pic);


        }
    }

    // 添加 setCards 方法以更新数据
    public void setCards(List<CardItem3> newCards) {
        this.cardList = newCards;
        notifyDataSetChanged();
    }

    public void addCard(CardItem3 card) {
        this.cardList.add(card);
        notifyItemInserted(cardList.size() - 1);
    }

    public void removeCard(int position) {
        if (position >= 0 && position < cardList.size()) {
            this.cardList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // 返回当前的卡片列表
    public List<CardItem3> getCards() {
        return this.cardList;
    }

    public void showEditContentDialog(final Context context, final int cardId, String currentContent,String currentLevel,CardItem3 card, final CardDatabaseHelper3 dbHelper, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑内容");

        // 使用自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_content2, null);

        // 获取输入框引用
        final TextInputEditText editTextContent = view.findViewById(R.id.editTextContent);
        final TextInputEditText editTextLevel = view.findViewById(R.id.editTextLevel);

        // 设置当前内容
        editTextContent.setText(currentContent);
        editTextContent.setSelection(currentContent.length()); // 将光标移动到文本末尾

        // 获取当前等级并设置到输入框
        // 假设您有一个方法可以获取当前卡片的等级
        // 例如：int currentLevel = dbHelper.getCardLevelById(cardId);
        // 这里为了示例，假设当前等级为1
        editTextLevel.setText(String.valueOf(currentLevel));
        editTextLevel.setSelection(currentLevel.length());
        builder.setView(view);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = editTextContent.getText().toString().trim();
                String newLevelStr = editTextLevel.getText().toString().trim();

                if (!newContent.isEmpty()) {
                    int newLevel;
                    try {
                        newLevel = Integer.parseInt(newLevelStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "请输入有效的等级", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 更新卡片内容
                    boolean isContentUpdated = dbHelper.updateCardContent(cardId, newContent);
                    // 更新卡片等级
                    boolean isLevelUpdated = dbHelper.updateCardLevel(cardId, newLevel);
                    if (isContentUpdated||isLevelUpdated) {
                        notifyItemChanged(position);
                    } else {
                        // 处理更新失败的情况
                        Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                    }
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

        builder.show();
    }








}



