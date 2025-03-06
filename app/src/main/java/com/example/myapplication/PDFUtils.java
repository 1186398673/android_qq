package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
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

    /**
     * 获取指定外部文件夹中所有 PDF 文件的名称
     *
     * @param context 上下文对象
     * @param folderPath 外部文件夹的路径
     * @return 包含 PDF 文件名称的 String 数组
     */
    public static String[] getPDFFileNamesFromExternalFolder(Context context, String folderPath) {
        List<String> pdfNames = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            Log.e("PDFUtils", "指定的文件夹不存在或不是一个目录: " + folderPath);
            return new String[0];
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                    pdfNames.add(file.getName());
                }
            }
        } else {
            Log.e("PDFUtils", "无法列出文件夹中的文件: " + folderPath);
        }

        return pdfNames.toArray(new String[0]);
    }

    /**
     * 从外部文件夹中的 PDF 文件获取封面
     *
     * @param context 上下文对象
     * @param folderPath 外部文件夹的路径
     * @param fileName PDF 文件的名称
     * @return 封面 Bitmap，如果失败则返回 null
     */
    public static Bitmap getPDFCoverFromExternalFolder(Context context, String folderPath, String fileName) {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        File pdfFile = new File(folderPath, fileName);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            Log.e("PDFUtils", "PDF 文件不存在或不是一个文件: " + pdfFile.getAbsolutePath());
            return null;
        }

        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, 0);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0, width, height);
            pdfiumCore.closeDocument(pdfDocument);
            fd.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDFUtils", "无法打开 PDF 文件: " + pdfFile.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * 将 PDF 文件移动到指定的文件夹
     *
     * @param context 上下文对象
     * @param sourcePath 源文件的完整路径
     * @param targetFolderPath 目标文件夹的路径
     * @return 移动是否成功
     */
    public static boolean movePDFToFolder(Context context, String sourcePath, String targetFolderPath) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            Log.e("FileUtils", "源文件不存在或不是一个文件: " + sourcePath);
            return false;
        }

        File targetFolder = new File(targetFolderPath);
        if (!targetFolder.exists()) {
            boolean created = targetFolder.mkdirs();
            if (!created) {
                Log.e("FileUtils", "无法创建目标文件夹: " + targetFolderPath);
                return false;
            }
        }

        File targetFile = new File(targetFolder, sourceFile.getName());

        // 检查目标文件是否已存在，避免覆盖
        if (targetFile.exists()) {
            Log.e("FileUtils", "目标文件已存在: " + targetFile.getAbsolutePath());
            return false;
        }

        boolean renamed = sourceFile.renameTo(targetFile);
        if (renamed) {
            Log.i("FileUtils", "文件已移动到: " + targetFile.getAbsolutePath());
        } else {
            Log.e("FileUtils", "无法移动文件到: " + targetFile.getAbsolutePath());
        }
        return renamed;
    }


    public static File createFolderInExternalFilesDir(Context context, String folderName) {
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS); // null 表示根目录
        File newFolder = new File(externalFilesDir, folderName);
        if (!newFolder.exists()) {
            boolean created = newFolder.mkdirs();
            if (created) {
                Log.i("FileUtils", "专用外部文件夹已创建: " + newFolder.getAbsolutePath());
                return newFolder;
            } else {
                Log.e("FileUtils", "无法创建专用外部文件夹: " + newFolder.getAbsolutePath());
                return null;
            }
        } else {
            Log.i("FileUtils", "专用外部文件夹已存在: " + newFolder.getAbsolutePath());
            return newFolder;
        }
    }

    /**
     * 从指定的文件路径获取 PDF 封面
     *
     * @param context 上下文对象
     * @param filePath PDF 文件的路径
     * @return 封面 Bitmap，如果失败则返回 null
     */
    public static Bitmap getPDFCover(Context context, String filePath) {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            Log.e("PDFUtils", "PDF 文件不存在或不是一个文件: " + filePath);
            return null;
        }

        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, 0);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0, width, height);
            pdfiumCore.closeDocument(pdfDocument);
            fd.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDFUtils", "无法打开 PDF 文件: " + filePath, e);
            return null;
        }
    }










}