package com.example.myapplication;







import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CardItem2;
import com.example.myapplication.R;

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
        holder.content.setText(card.getContent());
        holder.content.setOnClickListener(v->{
            CardDatabaseHelper3 dbHelper = new CardDatabaseHelper3(context);
            showEditContentDialog(context,card.getid(),card.getContent(),dbHelper,position);
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
        TextView content;
        public CardViewHolder3(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.cardContent2);

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

    public void showEditContentDialog(final Context context, final int cardId, String currentContent, final CardDatabaseHelper3 dbHelper, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑内容");

        // 设置输入框
        final EditText input = new EditText(context);
        input.setText(currentContent);
        input.setSelection(currentContent.length()); // 将光标移动到文本末尾
        builder.setView(input);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = input.getText().toString().trim();
                if (!newContent.isEmpty()) {
                    boolean isUpdated = dbHelper.updateCardContent(cardId, newContent);
                    if (isUpdated) {
                        // 更新数据源
                        cardList.get(position).setContent(newContent);
                        // 通知适配器数据已更改
                        notifyItemChanged(position);
                    } else {
                        // 处理更新失败的情况
                    }
                } else {
                    // 处理输入为空的情况
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



