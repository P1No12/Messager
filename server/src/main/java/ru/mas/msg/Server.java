package ru.mas.msg;
import ru.mas.msg.connection.Connection;
import ru.mas.msg.connection.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {
    private final ArrayList<Connection> listConnection = new ArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    private Server(){
        try (ServerSocket serverSocket = new ServerSocket(8080)){
            System.out.println("Server starter...");
            while (true){
                new Connection(this, serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void onConnectionReady(Connection connection) {
        listConnection.add(connection);
        sendAllUsers("Client connected: " + connection);
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String value) {
        sendAllUsers(value);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        listConnection.remove(connection);
        sendAllUsers("Client disconnected: "+ connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Exception: " + connection.toString() + e);
    }
    private void sendAllUsers(String value){
        System.out.println("value:" + value);
        for(Connection connection: listConnection){
            connection.sendMsg(value);
        }
    }
}
