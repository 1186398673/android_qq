package com.example.myapplication;





import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

        holder.content.setOnLongClickListener(v->{

            return  true;
        });
        holder.content.setOnClickListener(v->{
            Intent intent = new Intent(context, Cards_Activity2.class);
            intent.putExtra("parentid", card.getid());
            //Log.i("parentTitle",card.getTitle());
            context.startActivity(intent);
        });
        holder.content.setOnLongClickListener(v->{

            removeCard(position);
            CardDatabaseHelper2 dbHelper = new CardDatabaseHelper2(context);
            dbHelper.deleteCardById(card.getid());
            CardDatabaseHelper3 dbHelper3 = new CardDatabaseHelper3(context);
            dbHelper3.deleteCardsByParentId(card.getid());

            return  true;
        });
        holder.defineTextView.setText(card.getContentDefine());
        holder.meaningTextView.setText(card.getContentMeaning());
        holder.rangeTextView.setText(card.getContentRange());
        holder.exampleTextView.setText(card.getContentExample());
        holder.rangeTextView.setOnClickListener(v->{
            CardDatabaseHelper2 dbHelper = new CardDatabaseHelper2(context);
            showEditContentDialog(context,card,dbHelper,position);
        });
        holder.defineTextView.setOnClickListener(v->{
            CardDatabaseHelper2 dbHelper = new CardDatabaseHelper2(context);
            showEditContentDialog(context,card,dbHelper,position);
        });
        holder.meaningTextView.setOnClickListener(v->{
            CardDatabaseHelper2 dbHelper = new CardDatabaseHelper2(context);
            showEditContentDialog(context,card,dbHelper,position);
        });
        holder.exampleTextView.setOnClickListener(v->{
            CardDatabaseHelper2 dbHelper = new CardDatabaseHelper2(context);
            showEditContentDialog(context,card,dbHelper,position);
        });








    }

    // 返回列表大小
    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    // ViewHolder类,每个控件在item中的具体位置
    static class CardViewHolder2 extends RecyclerView.ViewHolder {
        TextView content;

        TextView defineTextView;
        TextView meaningTextView;
        TextView rangeTextView;
        TextView exampleTextView;
        public CardViewHolder2(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.cardContent);
            defineTextView = itemView.findViewById(R.id.cardContent_1);
            meaningTextView = itemView.findViewById(R.id.cardContent_2);
            rangeTextView = itemView.findViewById(R.id.cardContent_3);
            exampleTextView = itemView.findViewById(R.id.cardContent_4);

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

    public void removeCard(int position) {
        if (position >= 0 && position < cardList.size()) {
            this.cardList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // 返回当前的卡片列表
    public List<CardItem2> getCards() {
        return this.cardList;
    }

    public void showEditContentDialog(final Context context, final int cardId, String currentContent, final CardDatabaseHelper2 dbHelper, final int position) {
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
    public void showEditContentDialog(final Context context, final CardItem2 card, final CardDatabaseHelper2 dbHelper, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑内容");

        // 使用自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_content, null);

        // 获取输入框引用
        final TextInputEditText editTextContent = view.findViewById(R.id.editTextContent);
        final TextInputEditText editTextDefine = view.findViewById(R.id.editTextDefine);
        final TextInputEditText editTextMeaning = view.findViewById(R.id.editTextMeaning);
        final TextInputEditText editTextRange = view.findViewById(R.id.editTextRange);
        final TextInputEditText editTextExample = view.findViewById(R.id.editTextExample);

        // 设置输入框的文本
        editTextContent.setText(card.getContent());
        editTextDefine.setText(card.getContentDefine());
        editTextMeaning.setText(card.getContentMeaning());
        editTextRange.setText(card.getContentRange());
        editTextExample.setText(card.getContentExample());

        // 设置输入框的提示文本（可选）
        editTextContent.setHint("请输入标题");
        editTextDefine.setHint("请输入定义");
        editTextMeaning.setHint("请输入意义");
        editTextRange.setHint("请输入适用范围");
        editTextExample.setHint("请输入例子");

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

                if (!newContent.isEmpty()) {
                    // 更新卡片内容
                    card.setContent(newContent);
                    card.setContentDefine(newDefine);
                    card.setContentMeaning(newMeaning);
                    card.setContentRange(newRange);
                    card.setContentExample(newExample);

                    boolean isUpdated = dbHelper.updateCard(card);
                    if (isUpdated) {
                        // 更新数据源
                        cardList.set(position, card);
                        // 通知适配器数据已更改
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

        dialog.show();
    }








}


