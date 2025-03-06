package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class ReadActivity extends AppCompatActivity implements OnPageChangeListener {

    private TextView pageNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read);
        PDFView pdfView = findViewById(R.id.pdfView);
         pageNumberTextView = findViewById(R.id.pageNumberTextView);


         String filePath=getIntent().getStringExtra("filePath");
        pdfView.fromFile(new File(filePath)) // 从文件路径加载
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
                .load();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        // 更新页面指示器
        pageNumberTextView.setText((page + 1) + " / " + pageCount);
    }
}