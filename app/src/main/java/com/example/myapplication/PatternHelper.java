package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PatternHelper {
    private static final String KEY_PATTERN = "pattern";
    private SharedPreferences preferences;

    public PatternHelper(Context context) {
        preferences = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE);
    }

    // 保存图案（实际应加密）
    public void savePattern(String pattern) {
        preferences.edit().putString(KEY_PATTERN, pattern).apply();
    }

    // 验证图案
    public boolean verifyPattern(String inputPattern) {
        String savedPattern = preferences.getString(KEY_PATTERN, "");
        return savedPattern.equals(inputPattern);
    }

    // 检查是否已设置图案
    public boolean isPatternSet() {
        return preferences.contains(KEY_PATTERN);
    }
}