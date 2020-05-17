package ru.mas.msg.connection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connection {
    private final ConnectionListener connectionListener;
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader input;
    private final BufferedWriter output;

    public Connection(ConnectionListener connectionListener, String ip, int port) throws IOException {
        this(connectionListener, new Socket(ip, port));
    }

    public Connection(ConnectionListener connectionListener, Socket socket) throws IOException {
        this.connectionListener = connectionListener;
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionListener.onConnectionReady(Connection.this);
                    while (!rxThread.isInterrupted()){
                        connectionListener.onReceiveString(Connection.this, input.readLine());
                    }

                } catch (IOException e) {
                    connectionListener.onException(Connection.this, e);
                } finally {
                    connectionListener.onDisconnect(Connection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendMsg(String value) {
        try {
            output.write(value + "\r\n");
            output.flush();
        } catch (IOException e) {
            connectionListener.onException(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionListener.onException(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
