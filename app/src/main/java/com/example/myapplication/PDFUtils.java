package com.example.myapplication;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFUtils {

    /**
     * 获取 assets 文件夹中所有 PDF 文件的名称
     *
     * @param context 上下文对象
     * @return 包含 PDF 文件名称的 String 数组
     */
    public static String[] getPDFFileNames(Context context) {
        List<String> pdfNames = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("");
            if (files != null) {
                for (String file : files) {
                    if (file.toLowerCase().endsWith(".pdf")) {
                        pdfNames.add(file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常，例如记录日志或显示错误消息
        }
        return pdfNames.toArray(new String[0]);
    }

    public static Bitmap getPDFCoverFromAssets(Context context, String assetFileName) {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            AssetManager assetManager = context.getAssets();
            // 打开 Asset 的输入流
            InputStream inputStream = assetManager.open(assetFileName);
            // 将输入流转换为字节数组
            byte[] data = IOUtils.toByteArray(inputStream);
            // 使用 PdfiumCore 加载 PDF 文档
            PdfDocument pdfDocument = pdfiumCore.newDocument(data);
            // 打开第一页
            pdfiumCore.openPage(pdfDocument, 0);
            // 获取页面尺寸
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
            // 创建 Bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 渲染页面到 Bitmap
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0, width, height);
            // 关闭文档
            pdfiumCore.closeDocument(pdfDocument);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }






}