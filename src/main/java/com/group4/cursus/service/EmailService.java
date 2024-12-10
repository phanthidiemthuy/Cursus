package com.group4.cursus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendConfirmationEmail(String to, String token) {
        String subject = "Xác nhận địa chỉ email của bạn";
        String confirmationUrl = "http://localhost:8080/api/user/verify-email?token=" + token;
        String body = String.format("Dear %s,<br><br>Để xác thực địa chỉ email đã đăng ký vui lòng ấn <a href=\"%s\">vào đây</a>.<br>Java 07 Team 4<br>", to, confirmationUrl);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendConfirmationEmailUpdateProfile(String to, String token) {
        String subject = "Xác nhận cập nhật thông tin";
        String confirmationUrl = "http://localhost:8080/api/user/verify-email?token=" + token;
        String body = String.format("Dear %s,<br><br>Để xác nhận thông tin vừa cập nhật vui lòng ấn <a href=\"%s\">vào đây</a>.<br>Java 07 Team 4<br>", to, confirmationUrl);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendForgotPasswordEmail(String to, String token) {
        String subject = "Khôi phục mật khẩu";
        String resetUrl = "http://localhost:8080/api/user/reset-password?token=" + token;
        String body = String.format("Dear %s,<br><br>Vui lòng nhấn <a href=\"%s\">vào đây</a> để khôi phục tài khoản của bạn.<br>Java 07 Team 4<br>", to, resetUrl);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    public void sendApprovalEmail(String to) {
        String subject = "Chúc mừng bạn đã trở thành giáo viên tại Cursus.";
        String body = String.format("Dear %s,<br><br>" +
                "Chúc mừng bạn đã trở thành giáo viên tại Cursus. " +
                "Hãy truy cập vào hệ thống để tiến hành cung cấp, " +
                "phát triển khóa học của bạn.<br><br>" +
                "Java 07 Team 4", to);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendRejectionEmail(String to) {
        String subject = "Cảm ơn bạn đã chọn Cursus.";
        String body = String.format("Dear %s,<br><br>" +
                "Cảm ơn bạn đã chọn Cursus, chúng tôi đã xem xét và đánh giá bạn chưa phù hợp cho " +
                "những sản phẩm lần này.<br><br>" +
                "Chúng tôi sẽ hy vọng sẽ làm việc với bạn trong tương lai sắp tới.<br><br>" +
                "Java 07 Team 4", to);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void sendApplicationUnderReviewEmail(String to) {
        String subject = "Đơn đăng ký đang được xem xét";
        String body = String.format("Dear %s,<br><br>" +
                "Cảm ơn bạn đã đăng ký trở thành giảng viên tại hệ thống của chúng tôi. " +
                "Đơn đăng ký của bạn đang được xem xét. Chúng tôi sẽ thông báo cho bạn kết quả sớm nhất có thể.<br><br>" +
                "Trân trọng,<br>" , to);

        try {
            sendHtmlMessage(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
