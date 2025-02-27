package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment implements BookAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        // 初始化书籍列表
        bookList = new ArrayList<>();
        bookList.add(new Book(1, "书名1", R.mipmap.qq));
        bookList.add(new Book(2, "书名2", R.mipmap.qq));
        // 添加更多书籍

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

    }
}