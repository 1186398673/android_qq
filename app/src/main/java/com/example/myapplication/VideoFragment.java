package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment implements BookAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;

    private static final int PICK_EBOOK_FILE = 1;

    public VideoFragment() {
        // 必须有一个空的构造函数
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_video, container, false);


        Button button=view.findViewById(R.id.buttonaddbook);
        button.setOnClickListener(v->{

        });
        // 获取 assets 文件夹中的 PDF 文件名称
        String[] pdfFiles = PDFUtils.getPDFFileNames(getContext());
        int j=pdfFiles.length;
        int i;
        bookList = new ArrayList<>();
        for(i=0;i<j;i++)
        {
            bookList.add(new Book(i, pdfFiles[i], PDFUtils.getPDFCoverFromAssets(getContext(), pdfFiles[i])));
        }
        if (pdfFiles != null && pdfFiles.length > 0) {
            // 初始化书籍列表

            Log.i("PDF文件", "第一个PDF文件: " + pdfFiles[0]);
            Log.i("PDF文件", "第二个PDF文件: " + pdfFiles[1]);
            Log.i("PDF文件", "第三个PDF文件: " + pdfFiles[2]);
            // 你可以在这里处理 pdfFiles 数组，例如显示在 RecyclerView 中
        } else {
            Log.i("PDF文件", "没有找到 PDF 文件");
            Toast.makeText(getContext(), "没有找到 PDF 文件", Toast.LENGTH_SHORT).show();
        }



        // 获取 RecyclerView 并设置布局管理器
        recyclerView = view.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化适配器并设置点击监听器
        adapter = new BookAdapter(getContext(), bookList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(Book book) {
        Intent intent = new Intent(getActivity(), ReadActivity.class);
        intent.putExtra("booktitle", book.getTitle());
        startActivity(intent);
    }



}