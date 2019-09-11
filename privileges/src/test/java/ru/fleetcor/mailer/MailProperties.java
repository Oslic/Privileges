package ru.fleetcor.mailer;

import ru.fleetcor.properties.Prop;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ru.fleetcor.autotests.BaseTest.log;

/**
 * Created by Ivan.Zhirnov on 27.07.2018.
 */

public class MailProperties {

    //@PropertySet.Property("mail.smtp.server")
    protected static String server;

    //@PropertySet.Property("mail.smtp.port")
    private static String port;

    //@PropertySet.Property("mail.smtp.use-auth")
    private static boolean authorizationRequired;

    //@PropertySet.Property("mail.smtp.user")
    private static String user;

    //@PropertySet.Property("mail.smtp.password")
    private static String password;

    //@PropertySet.Property("mail.sender.address")
    private static String senderAddress;

    //@PropertySet.Property("mail.sender.name")
    private static String senderName;

    //@PropertySet.Property("mail.subject")
    private static String subject;

    //@PropertySet.Property("mail.download.url")

    private static String downloadUrl;

    public static void setMailProperties() {
        try {
            log.info("Считываем настройки для отправки сообщений");
            InputStream inputStream = Prop.class.getResourceAsStream("mail.properties");
            Properties propertie = new Properties();
            propertie.load(inputStream);

            server = propertie.getProperty("mail.smtp.server");
            port = propertie.getProperty("mail.smtp.port");
            authorizationRequired = Boolean.parseBoolean(propertie.getProperty("mail.smtp.use-auth"));
            user = propertie.getProperty("mail.smtp.user");
            password = propertie.getProperty("mail.smtp.password");
            senderAddress = propertie.getProperty("mail.sender.address");
            senderName = propertie.getProperty("mail.sender.name");
            subject = propertie.getProperty("mail.subject");
            downloadUrl = propertie.getProperty("mail.download.url");

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //@Parameters("mail.smtp.server")
    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
        MailProperties.server = server;
    }

    //@Parameters("mail.smtp.port")
    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        MailProperties.port = port;
    }

    //@Parameters("mail.smtp.use-auth")
    public static boolean isAuthorizationRequired() {
        return authorizationRequired;
    }

    public static void setAuthorizationRequired(boolean authorizationRequired) {
        MailProperties.authorizationRequired = authorizationRequired;
    }

    //@Parameters("mail.smtp.user")
    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        MailProperties.user = user;
    }

    //@Parameters("mail.smtp.password")
    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MailProperties.password = password;
    }

    //@Parameters("mail.sender.address")
    public static String getSenderAddress() {
        return senderAddress;
    }

    public static void setSenderAddress(String senderAddress) {
        MailProperties.senderAddress = senderAddress;
    }

    //@Parameters("mail.sender.name")
    public static String getSenderName() {
        return senderName;
    }

    public static void setSenderName(String senderName) {
        MailProperties.senderName = senderName;
    }

    //@Parameters("mail.subject")
    public static String getSubject() {
        return subject;
    }

    public static void setSubject(String subject) {
        MailProperties.subject = subject;
    }

    //@Parameters("mail.download.url")
    public static String getDownloadUrl() {
        return downloadUrl;
    }

    public static void setDownloadUrl(String downloadUrl) {
        MailProperties.downloadUrl = downloadUrl;
    }
}
