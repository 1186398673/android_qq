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

public class EmailSender extends AsyncTask<Void, Void, Boolean> {

    private String username = "1186398673@qq.com"; // 你的QQ邮箱
    private String password = "lltwgkgifolajgje"; // 你的授权码
    private String recipient = "3253169655@qq.com"; // 收件人邮箱
    private String subject = "多个文件夹下的CSV文件汇总";
    private String body = "请查收附件中的所有CSV文件。\n\n以下是每个CSV文件及其对应的文件夹路径：\n";

    @Override
    protected Boolean doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // 使用STARTTLS
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587"); // STARTTLS端口

        List<String> folderPaths = new ArrayList<>();
        folderPaths.add("/storage/emulated/0/Documents/CardData");
        folderPaths.add("/storage/emulated/0/Documents/CardData2");
        folderPaths.add("/storage/emulated/0/Documents/CardData3");
        // 根据需要添加更多文件夹路径

        // 收集所有CSV文件及其对应的文件夹路径
        List<File> filesToAttach = new ArrayList<>();
        Set<String> fileNames = new HashSet<>(); // 用于避免重复文件
        StringBuilder bodyBuilder = new StringBuilder(body);

        for (String folderPath : folderPaths) {
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
                if (file.isFile() && !fileNames.contains(file.getName())) {
                    filesToAttach.add(file);
                    fileNames.add(file.getName());
                    bodyBuilder.append("- ").append(file.getName()).append(" (来自 ").append(folderPath).append(")\n");
                    Log.i("EmailSender", "添加附件: " + file.getAbsolutePath());
                }
            }
        }

        if (filesToAttach.isEmpty()) {
            Log.e("EmailSender", "没有找到任何CSV文件");
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

            // 添加附件
            for (File file : filesToAttach) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                // 在文件名中添加文件夹信息
                String folderName = file.getParentFile().getName();
                String newFileName = folderName + "_" + file.getName();
                messageBodyPart.setFileName(newFileName);
                multipart.addBodyPart(messageBodyPart);
                Log.i("EmailSender", "添加附件: " + newFileName);
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