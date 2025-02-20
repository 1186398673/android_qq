package com.example.myapplication;





import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CardItem;
import com.example.myapplication.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardList;
    private List<CardItem> cards;
    private OnDeleteClickListener deleteClickListener;


    // 定义接口
    public interface OnDeleteClickListener {
        void onDeleteClick(int id);
    }

    // 设置监听器的方法
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }



    // 构造函数
    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    // 另一种构造函数（如果需要）
    public CardAdapter(Context context, List<CardItem> cardList) {
        this.cardList = cardList;
    }

    // 创建 ViewHolder
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem card = cardList.get(position);
        holder.icon.setImageResource(card.getIconResId());
        holder.title.setText(card.getTitle());
        holder.content.setText(card.getContent());
       holder.deleteButton.setOnClickListener(v -> {
           if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(card.getId());
            }
        });
    }

    // 返回列表大小
    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    // ViewHolder 类
    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView content;
        ImageButton deleteButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            deleteButton=itemView.findViewById(R.id.delectbtn);

        }
    }

    // 添加 setCards 方法以更新数据
    public void setCards(List<CardItem> newCards) {
        this.cardList = newCards;
        notifyDataSetChanged();
    }

    // 可选：添加方法来添加单个卡片
    public void addCard(CardItem card) {
        this.cardList.add(card);
        notifyItemInserted(cardList.size() - 1);
    }

    // 可选：添加方法来移除卡片
    public void removeCard(int position) {
        if (position >= 0 && position < cardList.size()) {
            this.cardList.remove(position);
            notifyItemRemoved(position);
        }
    }
}