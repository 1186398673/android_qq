package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender extends AsyncTask<Void, Void, Boolean> {

    private String username = "1186398673@qq.com"; // 你的QQ邮箱
    private String password = "lltwgkgifolajgje"; // 你的授权码
    private String recipient = "3253169655@qq.com"; // 收件人邮箱
    private String subject = "简单邮件";
    private String body = "xiaoxin is a good boy love the girl";

    @Override
    protected Boolean doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // 使用STARTTLS
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587"); // STARTTLS端口

        // 如果使用SSL，可以使用以下配置：
        // props.put("mail.smtp.socketFactory.port", "465");
        // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.port", "465");

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
            message.setText(body); // 设置邮件正文为纯文本

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