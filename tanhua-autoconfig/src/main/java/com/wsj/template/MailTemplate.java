package com.wsj.template;

import com.sun.mail.util.MailSSLSocketFactory;
import com.wsj.properties.MailProperties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发邮件工具类
 */
public class MailTemplate {
    private MailProperties mailProperties;

    public MailTemplate(MailProperties mailProperties){
        this.mailProperties = mailProperties;
    }

    /**
     * @param toAddress 收件人邮箱
     * @param text 邮件正文
     * @param title 标题
     */
    /* 发送验证信息的邮件 */
    public void sendMail(String toAddress, String text, String title){

        String user = mailProperties.getUser(); // 发件人称号，同邮箱地址
        String password = mailProperties.getPassword(); // 授权码，开启SMTP时显示

        // 配置JavaMail属性
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.auth", "true");

        // 创建一个会话对象
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // 创建一个消息对象
            Message message = new MimeMessage(session);
            // 设置发件人
            message.setFrom(new InternetAddress(user));
            // 设置收件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            // 设置主题
            message.setSubject(title);
            // 设置邮件内容
            message.setText(text);
            // 发送邮件
            Transport.send(message);
//            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
//            System.out.println("邮件发送失败！");
            e.printStackTrace();
        }
    }
}

