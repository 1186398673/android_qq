package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment implements BookAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;

    private static final int PICK_EBOOK_FILE = 1;

    private int j;



    private ActivityResultLauncher<Intent> filePickerLauncher;


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

            openFilePicker();
        });
        // 获取 assets 文件夹中的 PDF 文件名称
        String[] pdfFiles = PDFUtils.getPDFFileNames(getContext());
        j=pdfFiles.length;
        int i;
        bookList = new ArrayList<>();
        BookDatabaseHelper dbHelper = new BookDatabaseHelper(getContext());
        bookList=dbHelper.getAllBooks();
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
        intent.putExtra("filePath",book.getfilePath());
        startActivity(intent);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // 指定 MIME 类型为常见的电子书格式，您可以根据需要调整
        intent.setType("*/*");
        String[] mimeTypes = {"application/epub+zip", "application/pdf", "application/x-mobipocket-ebook"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // 可选：限制选择单个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(intent, PICK_EBOOK_FILE);
    }

    private void importEbook(Uri uri) {
        String fileName = getFileName(getContext(), uri);
        if (fileName == null) {
            Toast.makeText(getContext(), "无法获取文件名", Toast.LENGTH_SHORT).show();
            return;
        }

        File internalDir = getContext().getFilesDir();
        File importedFile = new File(internalDir, fileName);

        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(importedFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            Toast.makeText(getContext(), "电子书已导入", Toast.LENGTH_SHORT).show();
            // 更新书籍列表
            Bitmap cover = PDFUtils.getPDFCover(getContext(), importedFile.getAbsolutePath());
            Book newBook =new Book(j+1,fileName, importedFile.getAbsolutePath(),cover);
            bookList.add(newBook);
            adapter.notifyDataSetChanged();
            BookDatabaseHelper dbHelper = new BookDatabaseHelper(getContext());
            dbHelper.insertBook(newBook);



        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "导入电子书失败", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EBOOK_FILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("VideoFragment", "选中的文件 URI: " + uri.toString());
                // 处理导入的文件
                importEbook(uri);

            }
        }
    }






}