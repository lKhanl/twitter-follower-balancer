package dev.oguzhanercelik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private String build(String template, Map<String, Object> content) {
        Context context = new Context();
        for (String iter : content.keySet())
            context.setVariable(iter, content.get(iter));
        return templateEngine.process(template, context);
    }

    @Async
    public void sendMail(String to, String subject, String template, Map<String, Object> context)
            throws MailException, MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        helper.setText(build(template, context), true);

        helper.addInline("alt", new ClassPathResource("images/twitter.png"));
        helper.addInline("twitter", new ClassPathResource("images/twitter.png"));
        helper.addInline("linkedin", new ClassPathResource("images/linkedin.png"));
        helper.addInline("instagram", new ClassPathResource("images/instagram.png"));

        helper.setFrom(new InternetAddress(from, "no-reply"));
        helper.setTo(to);
        helper.setSubject(subject);
        javaMailSender.send(mimeMessage);
        log.info("sendMail(" + to + ") -> ( subject : " + subject + ", template : " + template + ", context : " + context);
    }

}
