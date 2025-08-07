package com.example.task;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity implements TaskDetailFragment.OnTaskUpdatedListener {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;

    // 日期相关
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Date selectedDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // 初始化数据库
        dbHelper = new TaskDatabaseHelper(this);

        recyclerView = findViewById(R.id.rv_tasks);
        viewPager = findViewById(R.id.view_pager);
        fab = findViewById(R.id.fab_add);

        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 添加日期选择按钮
        Button btnDatePicker = findViewById(R.id.btn_date_picker);
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
        fab.setOnClickListener(view -> showAddTaskDialog());
    }

    // 从数据库加载任务
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

        // 创建ViewPager适配器
        TaskPagerAdapter pagerAdapter = new TaskPagerAdapter(getSupportFragmentManager(), getLifecycle());

        // 获取所有任务并加载到ViewPager
        List<TaskItem> allTasks = dbHelper.getTasksByDate(selectedDate);
        for (TaskItem item : allTasks) {
            pagerAdapter.addFragment(TaskDetailFragment.newInstance(item));
        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(allTasks.indexOf(task), true);

        // 切换视图
        recyclerView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 使用带日期输入的对话框
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
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

            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
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

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            Button btnDatePicker = findViewById(R.id.btn_date_picker);
            btnDatePicker.setText("日期: " + dateFormat.format(selectedDate));
            loadTasks(); // 重新加载当天任务
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showTaskList() {
        viewPager.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskUpdated(TaskItem updatedTask) {
        // 更新数据库中的任务
        dbHelper.updateTask(updatedTask);
        // 重新加载数据
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库连接
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}