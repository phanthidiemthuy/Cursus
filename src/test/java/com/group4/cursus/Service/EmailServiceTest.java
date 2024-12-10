package com.group4.cursus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockMimeMessage(MimeMessage mimeMessage, String to, String subject, String content) throws MessagingException, IOException {
        when(mimeMessage.getRecipients(MimeMessage.RecipientType.TO)).thenReturn(InternetAddress.parse(to));
        when(mimeMessage.getSubject()).thenReturn(subject);
        when(mimeMessage.getContent()).thenReturn(content);
    }

    @Test
    void testSendConfirmationEmail() throws MessagingException, IOException {
        String to = "test@example.com";
        String token = "testToken";
        String subject = "Xác nhận địa chỉ email của bạn";
        String content = "testToken";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendConfirmationEmail(to, token);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }

    @Test
    void testSendConfirmationEmailUpdateProfile() throws MessagingException, IOException {
        String to = "test@example.com";
        String token = "testToken";
        String subject = "Xác nhận cập nhật thông tin";
        String content = "testToken";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendConfirmationEmailUpdateProfile(to, token);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }

    @Test
    void testSendForgotPasswordEmail() throws MessagingException, IOException {
        String to = "test@example.com";
        String token = "testToken";
        String subject = "Khôi phục mật khẩu";
        String content = "testToken";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendForgotPasswordEmail(to, token);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }

    @Test
    void testSendSimpleMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        emailService.sendSimpleMessage(to, subject, text);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(text, capturedMessage.getText());
    }

    @Test
    void testSendApprovalEmail() throws MessagingException, IOException {
        String to = "test@example.com";
        String subject = "Chúc mừng bạn đã trở thành giáo viên tại Cursus.";
        String content = "Chúc mừng bạn đã trở thành giáo viên tại Cursus";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendApprovalEmail(to);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }

    @Test
    void testSendRejectionEmail() throws MessagingException, IOException {
        String to = "test@example.com";
        String subject = "Cảm ơn bạn đã chọn Cursus.";
        String content = "Cảm ơn bạn đã chọn Cursus";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendRejectionEmail(to);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }

    @Test
    void testSendApplicationUnderReviewEmail() throws MessagingException, IOException {
        String to = "test@example.com";
        String subject = "Đơn đăng ký đang được xem xét";
        String content = "Đơn đăng ký đang được xem xét";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        mockMimeMessage(mimeMessage, to, subject, content);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendApplicationUnderReviewEmail(to);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMimeMessage = mimeMessageCaptor.getValue();

        assertEquals(to, capturedMimeMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, capturedMimeMessage.getSubject());
        assertTrue(capturedMimeMessage.getContent().toString().contains(content));
    }
}
