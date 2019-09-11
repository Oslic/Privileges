package ru.fleetcor.mailer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static ru.fleetcor.autotests.BaseTest.browser;
import static ru.fleetcor.autotests.BaseTest.log;
import static ru.fleetcor.autotests.BaseTest.logPath;

/**
 * Created by Ivan.Zhirnov on 27.07.2018.
 */

public class Mailer {

    private Session mailSession;
    private InternetAddress sender;

    private Properties initSessionProps(){
        MailProperties.setMailProperties();
        return new Properties() {
            {
                put("mail.smtp.auth", MailProperties.isAuthorizationRequired());
                put("mail.smtp.starttls.enable", true);
                put("mail.smtp.host", MailProperties.getServer());
                put("mail.smtp.port", MailProperties.getPort());
            }
        };
    }

    private Session openMailingSession(){
        if (true){
            return Session.getDefaultInstance(initSessionProps(),
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(MailProperties.getUser(),MailProperties.getPassword());
                        }
                    });
        }
        return Session.getDefaultInstance(initSessionProps(), null);
    }

    private Session getMailSession(){
        if (mailSession == null) {
            mailSession = openMailingSession();
        }
        return mailSession;
    }

    private InternetAddress getMessageSender() throws UnsupportedEncodingException {
        if (sender == null){
            sender = new InternetAddress(MailProperties.getSenderAddress(), MailProperties.getSenderName());
        }
        System.out.println(sender);
        return sender;
    }

    private MimeMultipart createMessageMultipart() throws MessagingException, IOException {
        MimeMultipart multipart = new MimeMultipart();
        File dir = new File("errors");
        File files[] = dir.listFiles();
        log.info("Прикрепляем скриншоты к письму");
        for (int i = 0; i < files.length; i++) {
            MimeBodyPart messageBodyPart  = new MimeBodyPart();
            messageBodyPart.attachFile(files[i]);
            multipart.addBodyPart(messageBodyPart);
        }
        File logFile = new File(logPath);
        log.info("Прикрепляем лог к письму");
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.attachFile(logFile);
        multipart.addBodyPart(mimeBodyPart);
        return multipart;
    }


    private MimeMessage createMimeMessage(final Message message) throws MessagingException, IOException {
        MimeMessage mimeMessage = new MimeMessage(getMailSession()) {
            {
                log.info("Задаем значение \"От кого\"");
                setFrom(new InternetAddress(MailProperties.getUser()));
                log.info("Задаем значение \"Кому\"");
                setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(MailProperties.getSenderAddress()));
                log.info("Создаем тело письма");
                setContent(createMessageMultipart());
                log.info("Задаем тему письма");
                setSubject("Errors in Autotests with " + browser);
            }
        };
        return mimeMessage;
    }


    private void transportMessage(Session session, MimeMessage msg) throws MessagingException {
        Transport tr = session.getTransport("smtp");
        log.info("Устанавливаем соединение");
        tr.connect(MailProperties.getServer(), MailProperties.getUser(), MailProperties.getPassword());
        log.info("Сохраняем сообщение");
        msg.saveChanges();
        log.info("Отправляем письмо");
        tr.sendMessage(msg, InternetAddress.parse(MailProperties.getSenderAddress()));
        log.info("Закрываем соединение");
        tr.close();
        log.info("Сообщение отправлено");
    }

    public void sendMail() throws MessagingException, IOException {
        log.info("Открываем сессию для отправки сообщения");
        Session session = openMailingSession();
        Message message = new Message();
        MimeMessage mimeMessage = createMimeMessage(message);
        transportMessage(session, mimeMessage);
    }

}
