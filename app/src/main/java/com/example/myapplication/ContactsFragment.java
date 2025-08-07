package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ContactsFragment extends Fragment {

    // 常量定义
    private static final String PREFS_NAME = "ContactHistory";
    private static final String KEY_HISTORY = "history";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());

    // 视图组件
    private Button btnStartDate;
    private Button btnEndDate;
    private EditText etUserInput;
    private Button btnSave;
    private TextView tvHistory;

    private Button btnDelete;

    // 日历实例
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图组件
        btnStartDate = view.findViewById(R.id.btn_start_date);
        btnEndDate = view.findViewById(R.id.btn_end_date);
        etUserInput = view.findViewById(R.id.et_user_input);
        btnSave = view.findViewById(R.id.btn_save);
        tvHistory = view.findViewById(R.id.tv_history);

        // 设置按钮点击事件
        btnSave.setOnClickListener(v -> saveContent());
        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
        btnDelete = view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(v -> clearContent());

        // 加载历史记录
        loadHistory();
        updateDateButtons();
        updateContent();
    }
    private void clearContent() {
        // 清空所有内容
        tvHistory.setText("");
        etUserInput.setText("");
        saveToSharedPrefs("");
        Toast.makeText(getContext(), "已清除所有记录", Toast.LENGTH_SHORT).show();
    }

    // 显示日期选择对话框
    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateButtons();
            updateContent();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    // 更新日期按钮的文本
    private void updateDateButtons() {
        btnStartDate.setText(DATE_FORMAT.format(startCalendar.getTime()));
        btnEndDate.setText(DATE_FORMAT.format(endCalendar.getTime()));
    }

    // 更新输入框的提示信息
    private void updateContent() {
        if (startCalendar.after(endCalendar)) {
            Toast.makeText(getContext(), "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        String dateRange = btnStartDate.getText() + " 至 " + btnEndDate.getText();
        etUserInput.setHint("在此输入 " + dateRange + " 的内容");
    }

    // 保存用户输入的内容
    private void saveContent() {
        String inputText = etUserInput.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(getContext(), "输入内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeRange = btnStartDate.getText() + " 至 " + btnEndDate.getText();
        String newRecord = timeRange + "   " + inputText + "\n\n";

        StringBuilder historyBuilder = new StringBuilder(newRecord);
        historyBuilder.append(tvHistory.getText());

        tvHistory.setText(historyBuilder.toString());
        etUserInput.setText("");
        saveToSharedPrefs(historyBuilder.toString());
    }

    // 保存历史记录到 SharedPreferences
    private void saveToSharedPrefs(String history) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_HISTORY, history).apply();
    }

    // 加载历史记录
    private void loadHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");
        tvHistory.setText(history);
    }
}