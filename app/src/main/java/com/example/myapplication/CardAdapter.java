package com.example.myapplication;





import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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

import com.example.myapplication.CardItem;
import com.example.myapplication.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardList;
    private Context context;






    // 构造函数
    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    // 另一种构造函数（如果需要）
    public CardAdapter(Context context, List<CardItem> cardList) {
        this.cardList = cardList;
        this.context=context;



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


        holder.title.setOnClickListener(v -> {

            CardDatabaseHelper2 dbHelper2 = new CardDatabaseHelper2(context);
            String parentTitle= card.getTitle();
            List<CardItem2> cardList2 = dbHelper2.getCardsByParentTitle(parentTitle);
            CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
            dbHelper.updateCardContent(card.getId(), "卡片数"+cardList2.size());
            notifyDataSetChanged();
            Intent intent = new Intent(context, Cards_Activity.class);
            intent.putExtra("parentTitle", card.getTitle());
            //Log.i("parentTitle",card.getTitle());
            context.startActivity(intent);

            

        });

        holder.title.setOnLongClickListener(v->{
            CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
            showRenameDialog(v.getContext(), card.getId(), dbHelper,card.getTitle());
            //CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
            //showEditContentDialog(context, card.getId(), card.getContent(), dbHelper, position);
            return true;
        });

        holder.icon.setOnLongClickListener(v->{
            CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
            dbHelper.deleteCardById(card.getId());
            CardDatabaseHelper2 dbHelper2 = new CardDatabaseHelper2(context);
            dbHelper2.deleteCardsByParentTitle(card.getTitle());
            removeCard(position);
            return true;
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



    // 方法用于显示重命名对话框
    public void showRenameDialog(final Context context, final int cardId, final CardDatabaseHelper dbHelper,final String oldtitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("重命名卡片");

        // 设置输入框
        final EditText input = new EditText(context);
        input.setHint("请输入新的卡片名称");
        builder.setView(input);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = input.getText().toString().trim();
                if (!newTitle.isEmpty()) {
                    boolean isRenamed = dbHelper.renameCard(cardId, newTitle);
                    CardDatabaseHelper2 dbHelper2 = new CardDatabaseHelper2(context);
                    dbHelper2.updateParentTitle(oldtitle,newTitle);
                    if (isRenamed) {
                        CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
                        List<CardItem> cardList = dbHelper.getCardList();
                        setCards(cardList);
                    } else {
                        // 处理重命名失败的情况
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


    // 方法用于显示编辑内容的对话框
    public void showEditContentDialog(final Context context, final int cardId, String currentContent, final CardDatabaseHelper dbHelper, final int position) {
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