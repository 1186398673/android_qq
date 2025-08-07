package com.example.myapplication;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.task.TaskAdapter;
import com.example.task.TaskDatabaseHelper;
import com.example.task.TaskDetailFragment;
import com.example.task.TaskItem;
import com.example.task.TaskPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChannelFragment extends Fragment implements TaskDetailFragment.OnTaskUpdatedListener,TaskDetailFragment.OnBackPressedListener{

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;

    private Button btnDatePicker;

    private TextView MTextView;

    // 日期相关
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Date selectedDate = new Date();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.activity_task, container, false);
        // 初始化数据库
        dbHelper = new TaskDatabaseHelper(getContext());

        recyclerView = view.findViewById(R.id.rv_tasks);
        viewPager = view.findViewById(R.id.view_pager);
        fab = view.findViewById(R.id.fab_add);
        MTextView=view.findViewById(R.id.title);

        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 添加日期选择按钮
        btnDatePicker = view.findViewById(R.id.btn_date_picker);
        btnDatePicker.setText("日期: " + dateFormat.format(selectedDate));
        btnDatePicker.setOnClickListener(v -> showDatePicker());

        // 初始加载当天任务
        loadTasks();

        // 设置点击监听器
        adapter.setOnItemClickListener(task -> showTaskDetail(task));

        // 添加滑动删除功能
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        TaskItem task = adapter.getItems().get(position);
                        // 从数据库删除
                        dbHelper.deleteTask(task.getId());
                        // 重新加载数据
                        loadTasks();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 添加按钮点击事件
        fab.setOnClickListener(v-> {
            showAddTaskDialog();
        });


        return view;
    }
    private void loadTasks() {
        List<TaskItem> tasks = dbHelper.getTasksByDate(selectedDate);
        if (adapter == null) {
            adapter = new TaskAdapter(tasks,dbHelper);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateTasks(tasks);
        }
    }

    private void showTaskDetail(TaskItem task) {
        // 创建详情Fragment并传递数据
        TaskDetailFragment detailFragment = TaskDetailFragment.newInstance(task);
        detailFragment.setOnTaskUpdatedListener(this);
        detailFragment.setOnBackPressedListener(this);
        
        // 创建ViewPager适配器
        TaskPagerAdapter pagerAdapter = new TaskPagerAdapter(getParentFragmentManager(), getLifecycle());

        // 获取所有任务并加载到ViewPager（添加空检查）
        List<TaskItem> allTasks = dbHelper.getTasksByDate(selectedDate);
        if (allTasks == null || allTasks.isEmpty()) {
            Toast.makeText(getContext(), "当前日期无任务", Toast.LENGTH_SHORT).show();
            return; // 提前返回避免空指针
        }
        
        for (TaskItem item : allTasks) {
            TaskDetailFragment fragment = TaskDetailFragment.newInstance(item);
            fragment.setOnTaskUpdatedListener(this);
            fragment.setOnBackPressedListener(this);
            pagerAdapter.addFragment(fragment);
        }

        // 添加索引有效性检查
        int position = allTasks.indexOf(task);
        if (position == -1) position = 0;

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position, true); // 使用验证后的position

        // 切换视图
        recyclerView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
        btnDatePicker.setVisibility(View.GONE);
        MTextView.setVisibility(View.GONE);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // 使用带日期输入的对话框
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        EditText etTitle = view.findViewById(R.id.et_title);
        EditText etContent = view.findViewById(R.id.et_content);

        // 可选：添加日期选择按钮
        Button btnSetDate = view.findViewById(R.id.btn_set_date);
        final Date[] selectedTaskDate = {selectedDate}; // 默认使用当前筛选日期

        btnSetDate.setText("日期: " + dateFormat.format(selectedTaskDate[0]));
        btnSetDate.setOnClickListener(v -> {
            // 显示日期选择器
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedTaskDate[0]);

            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                selectedTaskDate[0] = calendar.getTime();
                btnSetDate.setText("日期: " + dateFormat.format(selectedTaskDate[0]));
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        builder.setView(view);
        builder.setPositiveButton("添加", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (!title.isEmpty()) {
                // 生成新ID
                List<TaskItem> allTasks = dbHelper.getAllTasks();
                int newId = allTasks.size() > 0 ?
                        allTasks.get(allTasks.size() - 1).getId() + 1 : 1;

                // 创建带日期的新任务
                TaskItem newTask = new TaskItem(newId, title, content, selectedTaskDate[0],false);

                // 插入数据库
                dbHelper.insertTask(newTask);

                // 重新加载数据
                loadTasks();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 显示日期选择器
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            // 修复：使用类成员变量btnDatePicker代替重新findViewById
            Button btnDatePicker = getView().findViewById(R.id.btn_date_picker); // 需要先在onCreateView保存按钮引用
            btnDatePicker.setText("日期: " + dateFormat.format(selectedDate));
            loadTasks();
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showTaskList() {
        viewPager.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        btnDatePicker.setVisibility(View.VISIBLE);
        MTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskUpdated(TaskItem updatedTask) {
        // 更新数据库中的任务
        dbHelper.updateTask(updatedTask);
        // 重新加载数据
        loadTasks();
        // 添加ViewPager刷新
        if(viewPager.getAdapter() != null){
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        showTaskList();
        // 确保回到列表后刷新数据
        loadTasks();
    }




}