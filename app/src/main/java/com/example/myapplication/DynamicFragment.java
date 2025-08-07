package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DynamicFragment extends Fragment {
    private EditText etParentId;
    private TextView dailyQuoteText;
    private CardDatabaseHelper3 dbHelper;

    private Button btn_query;

    private boolean isvisi=true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CardDatabaseHelper3(getActivity());
    }

    private Handler mHandler;
    private Runnable mRefreshRunnable;
    private static final long REFRESH_INTERVAL = 60 * 1000; // 1分钟刷新一次
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LAST_PARENT_ID = "last_parent_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        etParentId = view.findViewById(R.id.et_parent_id);
        dailyQuoteText = view.findViewById(R.id.daily_quote_text);
        dailyQuoteText.setMovementMethod(new ScrollingMovementMethod()); // 添加滚动支持
        btn_query = view.findViewById(R.id.btn_query);



        // 读取存储的parentId
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastParentId = prefs.getString(KEY_LAST_PARENT_ID, "");
        etParentId.setText(lastParentId);
        queryDailyContent(lastParentId);
        etParentId.setVisibility(View.GONE);
        btn_query.setVisibility(View.GONE);
        btn_query.setOnClickListener(v -> {
            String parentId = etParentId.getText().toString().trim();
            if (!parentId.isEmpty()) {
                // 存储当前parentId
                prefs.edit().putString(KEY_LAST_PARENT_ID, parentId).apply();
                queryDailyContent(parentId);
            }
        });

        dailyQuoteText.setOnClickListener(v->{
            if(isvisi){
                etParentId.setVisibility(View.GONE);
                btn_query.setVisibility(View.GONE);
                isvisi=false;
            }
            else {
                etParentId.setVisibility(View.VISIBLE);
                btn_query.setVisibility(View.VISIBLE);
                isvisi=true;
            }

        });
        
        // 添加定时任务
        mHandler = new Handler();
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                String currentParentId = etParentId.getText().toString().trim();
                if (!currentParentId.isEmpty()) {
                    queryDailyContent(currentParentId);
                }
                mHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        mHandler.postDelayed(mRefreshRunnable, REFRESH_INTERVAL);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 移除定时任务防止内存泄漏
        if (mHandler != null && mRefreshRunnable != null) {
            mHandler.removeCallbacks(mRefreshRunnable);
        }
    }

    private void queryDailyContent(String parentId) {
        new Thread(() -> {
            String content = dbHelper.getDailyContentByParentId(parentId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (dailyQuoteText != null) {
                        dailyQuoteText.setText(content);
                    }
                });
            }
        }).start();
    }
}