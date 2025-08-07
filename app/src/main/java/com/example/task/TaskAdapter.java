package com.example.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
    private List<TaskItem> items;
    private OnItemClickListener listener;
    private TaskDatabaseHelper dbHelper;
    public interface OnItemClickListener {
        void onItemClick(TaskItem task);
    }

    public TaskAdapter(List<TaskItem> items, TaskDatabaseHelper dbHelper) {
        this.items = items;
        this.dbHelper = dbHelper;
    }

    public List<TaskItem> getItems() {
        return items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void updateTasks(List<TaskItem> newItems) {
        items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TaskItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.cbCompleted.setChecked(item.isCompleted());
        holder.itemView.setSelected(item.isCompleted());

        if(item.isCompleted())
        {
            holder.textView.setText("已完成");
            holder.itemView.setSelected(true);
        }
        else {
            holder.textView.setText("未完成");
            holder.itemView.setSelected(false);
        }
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            dbHelper.updateTask(item);
            holder.itemView.post(() ->notifyDataSetChanged());

            //updateTasks(items);
        });
        holder.content.setVisibility(View.GONE);



        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        CheckBox cbCompleted;

        TextView textView;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            cbCompleted = itemView.findViewById(R.id.cb_completed);
            textView=itemView.findViewById(R.id.text_0);

        }
    }
}