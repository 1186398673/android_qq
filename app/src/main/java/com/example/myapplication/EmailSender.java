package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender extends AsyncTask<Void, Void, Boolean> {

    private String username = "1186398673@qq.com"; // 你的QQ邮箱
    private String password = "lltwgkgifolajgje"; // 你的授权码
    private String recipient = "3253169655@qq.com"; // 收件人邮箱
    private String subject = "多个文件夹下的CSV文件和图片文件汇总";
    private String body = "请查收附件中的所有CSV文件和图片文件。\n\n以下是每个文件及其对应的文件夹路径：\n";

    @Override
    protected Boolean doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // 使用STARTTLS
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587"); // STARTTLS端口

        List<String> csvFolderPaths = new ArrayList<>();
        csvFolderPaths.add("/storage/emulated/0/Documents/CardData");
        csvFolderPaths.add("/storage/emulated/0/Documents/CardData2");
        csvFolderPaths.add("/storage/emulated/0/Documents/CardData3");
        // 根据需要添加更多CSV文件夹路径

        List<String> imageFolderPaths = new ArrayList<>();
        imageFolderPaths.add("/storage/emulated/0/Documents/CardData3_pic");
        // 根据需要添加更多图片文件夹路径

        // 收集所有CSV文件及其对应的文件夹路径
        List<File> csvFilesToAttach = new ArrayList<>();
        Set<String> csvFileNames = new HashSet<>();
        StringBuilder bodyBuilder = new StringBuilder(body);

        for (String folderPath : csvFolderPaths) {
            File exportDir = new File(folderPath);
            if (!exportDir.exists() || !exportDir.isDirectory()) {
                Log.e("EmailSender", "文件夹不存在或不是一个目录: " + folderPath);
                continue;
            }

            File[] csvFiles = exportDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (csvFiles == null || csvFiles.length == 0) {
                Log.e("EmailSender", "文件夹中没有CSV文件: " + folderPath);
                continue;
            }

            for (File file : csvFiles) {
                if (file.isFile() && !csvFileNames.contains(file.getName())) {
                    csvFilesToAttach.add(file);
                    csvFileNames.add(file.getName());
                    bodyBuilder.append("- ").append(file.getName()).append(" (来自 ").append(folderPath).append(")\n");
                    Log.i("EmailSender", "添加CSV附件: " + file.getAbsolutePath());
                }
            }
        }

        // 收集所有图片文件及其对应的文件夹路径
        List<File> imageFilesToAttach = new ArrayList<>();
        Set<String> imageFileNames = new HashSet<>();

        for (String folderPath : imageFolderPaths) {
            File imageDir = new File(folderPath);
            if (!imageDir.exists() || !imageDir.isDirectory()) {
                Log.e("EmailSender", "文件夹不存在或不是一个目录: " + folderPath);
                continue;
            }

            File[] imageFiles = imageDir.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png") || lowerName.endsWith(".gif");
            });

            if (imageFiles == null || imageFiles.length == 0) {
                Log.e("EmailSender", "文件夹中没有图片文件: " + folderPath);
                continue;
            }

            for (File file : imageFiles) {
                if (file.isFile() && !imageFileNames.contains(file.getName())) {
                    imageFilesToAttach.add(file);
                    imageFileNames.add(file.getName());
                    bodyBuilder.append("- ").append(file.getName()).append(" (来自 ").append(folderPath).append(")\n");
                    Log.i("EmailSender", "添加图片附件: " + file.getAbsolutePath());
                }
            }
        }

        if (csvFilesToAttach.isEmpty() && imageFilesToAttach.isEmpty()) {
            Log.e("EmailSender", "没有找到任何CSV或图片文件");
            return false;
        }

        // 更新邮件正文
        body = bodyBuilder.toString();

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            // 创建Multipart对象
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            // 添加CSV附件
            for (File file : csvFilesToAttach) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                // 在文件名中添加文件夹信息
                String folderName = file.getParentFile().getName();
                String newFileName = folderName + "_" + file.getName();
                messageBodyPart.setFileName(newFileName);
                multipart.addBodyPart(messageBodyPart);
                Log.i("EmailSender", "添加CSV附件: " + newFileName);
            }

            // 添加图片附件
            for (File file : imageFilesToAttach) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                // 在文件名中添加文件夹信息
                String folderName = file.getParentFile().getName();
                String newFileName = folderName + "_" + file.getName();
                messageBodyPart.setFileName(newFileName);
                multipart.addBodyPart(messageBodyPart);
                Log.i("EmailSender", "添加图片附件: " + newFileName);
            }

            // 设置邮件内容
            message.setContent(multipart);

            // 发送邮件
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            Log.e("EmailSender", "发送邮件失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.i("EmailSender", "邮件发送成功");
        } else {
            Log.i("EmailSender", "邮件发送失败");
        }
    }
}