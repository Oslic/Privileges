package ru.fleetcor.util;

import ru.fleetcor.properties.Prop;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static ru.fleetcor.autotests.BaseTest.log;


public class ConnectionFactory {

    private static String driverDB;
    private static String server;
    private static String port;
    private static String sid;
    private static String username;
    private static String password;

    public ConnectionFactory(String driverDB, String server, String port, String sid, String username, String password)  {
        this.driverDB = driverDB;
        this.server = server;
        this.port = port;
        this.sid = sid;
        this.username = username;
        this.password = password;
    }

    public static ConnectionFactory loadFromFile() {
        try  {
            InputStream inputStream = Prop.class.getResourceAsStream("data_base.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String driverDB = properties.getProperty("driverDB");
            String server = properties.getProperty("server");
            String port = properties.getProperty("port");
            String sid = properties.getProperty("sid");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            ConnectionFactory connectionFactory = new ConnectionFactory(driverDB, server, port, sid, username, password);
            inputStream.close();
            return connectionFactory;
        } catch (IOException e) {
            throw new RuntimeException("IOException in loadDataBaseProps");
        }
    }


    private static Connection connection;
    private static boolean isConnected = false;

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        log.info("Получаем соединение с БД");
        String url = "jdbc:oracle:thin:@" + server + ":" + port + ":" + sid;
        Class.forName(driverDB);
        connection = DriverManager.getConnection(url, username, password);
        isConnected = !connection.equals(null);
        return connection;
    }
}