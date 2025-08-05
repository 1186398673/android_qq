package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class ReadActivity extends AppCompatActivity implements OnPageChangeListener {
    private PDFView pdfView;
    private TextView pageNumberTextView;
    private List<PdfDocument.Bookmark> bookmarks; // 新增目录集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read);
        pdfView = findViewById(R.id.pdfView);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);

        String filePath = getIntent().getStringExtra("filePath");
        File file = new File(filePath);
        
        pdfView.fromFile(file)
                .enableSwipe(true) // 启用滑动
                .swipeHorizontal(false) // 垂直滑动
                .enableDoubletap(true) // 启用双击缩放
                .enableAnnotationRendering(true) // 启用注释渲染
                .password(null) // 如果 PDF 有密码，设置为密码字符串
                .scrollHandle(new DefaultScrollHandle(this)) // 添加滚动条
                .enableAntialiasing(true) // 启用抗锯齿
                .spacing(10) // 设置页面间距
                .autoSpacing(true) // 自动调整页面间距
                .pageFitPolicy(FitPolicy.BOTH) // 页面适应策略：宽度和高度都适应
                .fitEachPage(true) // 每个页面都适应屏幕
                .onLoad(nbPages -> {
                    // 新增目录解析（在PDF加载完成后执行）
                    bookmarks = pdfView.getTableOfContents();
                    setupNavigationDrawer(); // 初始化目录导航
                })
                .load();
    }

    // 新增目录导航实现
    private void setupNavigationDrawer() {
        findViewById(R.id.btn_show_toc).setOnClickListener(v -> {
            // 创建目录弹窗或侧边栏导航
            showTableOfContents(bookmarks);
        });
    }

    // 新增目录跳转方法
    private void jumpToPage(int page) {
        pdfView.jumpTo(page, true);
    }

    // 新增目录弹窗显示方法
    private void showTableOfContents(List<PdfDocument.Bookmark> bookmarks) {
        if (bookmarks == null || bookmarks.isEmpty()) {
            Toast.makeText(this, "该文档没有目录", Toast.LENGTH_SHORT).show();
            return;
        }
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("目录导航");
        
        // 处理可能存在的空标题
        String[] items = new String[bookmarks.size()];
        for (int i = 0; i < bookmarks.size(); i++) {
            String title = bookmarks.get(i).getTitle();
            items[i] = TextUtils.isEmpty(title) ? "未命名章节" : title;
        }
        
        // 添加滚动条处理长列表
        builder.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, items), (dialog, which) -> {
                // 修正页码偏移（PDFium从0开始，显示页码+1）
                int targetPage = (int) bookmarks.get(which).getPageIdx();
                jumpToPage(targetPage);
                // 更新页面指示器
                onPageChanged(targetPage, pdfView.getPageCount());
            });
        
        builder.setNegativeButton("关闭", null);
        AlertDialog dialog = builder.create();
        
        // 设置对话框最大高度
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        dialog.getWindow().setLayout(metrics.widthPixels * 2 / 3, metrics.heightPixels * 3 / 4);
        dialog.show();
    }
    
    // 保持原有onPageChanged方法不变
    @Override
    public void onPageChanged(int page, int pageCount) {
        // 更新页面指示器
        pageNumberTextView.setText((page + 1) + " / " + pageCount);
    }
}