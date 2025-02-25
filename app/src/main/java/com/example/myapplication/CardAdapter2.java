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

public class CardAdapter2 extends RecyclerView.Adapter<CardAdapter2.CardViewHolder2> {

    private List<CardItem2> cardList;//用于存储要显示的卡片数据
    private Context context;





    // 另一种构造函数（如果需要）
    public CardAdapter2(Context context, List<CardItem2> cardList) {
        this.cardList = cardList;
        this.context=context;



    }

    // 创建 ViewHolder,加载布局文件
    @NonNull
    @Override
    public CardViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card, parent, false);

        return new CardViewHolder2(view);
    }

    // 绑定数据到 ViewHolder，为子项赋值，通过position获取实例，设置卡片行为
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder2 holder, int position) {
        CardItem2 card = cardList.get(position);
        holder.content.setText(card.getContent());






    }

    // 返回列表大小
    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    // ViewHolder类,每个控件在item中的具体位置
    static class CardViewHolder2 extends RecyclerView.ViewHolder {
        TextView content;
        public CardViewHolder2(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.cardContent);

        }
    }

    // 添加 setCards 方法以更新数据
    public void setCards(List<CardItem2> newCards) {
        this.cardList = newCards;
        notifyDataSetChanged();
    }

    public void addCard(CardItem2 card) {
        this.cardList.add(card);
        notifyItemInserted(cardList.size() - 1);
    }

    // 返回当前的卡片列表
    public List<CardItem2> getCards() {
        return this.cardList;
    }








    }


