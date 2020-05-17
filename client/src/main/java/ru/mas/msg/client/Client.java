package ru.mas.msg.client;

import ru.mas.msg.connection.Connection;
import ru.mas.msg.connection.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, ConnectionListener{

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final JTextArea log= new JTextArea();
    private final JTextField fieldNickName = new JTextField("Input name");
    private final JTextField fieldInput = new JTextField();
    private JButton button= new JButton("Отправить");
    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private Client(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        button.addActionListener(new ActionListener() {
                                     @Override
                                     public void actionPerformed(ActionEvent e) {
                                         String msg = fieldInput.getText();
                                         if (msg.equals("")) return;
                                         fieldInput.setText(null);
                                         connection.sendMsg(fieldNickName.getText() + msg);
                                     }
                                 }
        );
        add(button, BorderLayout.EAST);

        setVisible(true);

        try {
            connection = new Connection(this,IP, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }

    }
    private synchronized void printMessage(String msg) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    log.append(msg+"\n");
                    log.setCaretPosition(log.getDocument().getLength());
                }
            });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendMsg(fieldNickName.getText()+ ": " + msg);

    }

    @Override
    public void onConnectionReady(Connection Connection) {
        printMessage("Connection ready...");
    }
    @Override
    public void onReceiveString(Connection Connection, String value) {
        printMessage(value);
    }
    @Override
    public void onDisconnect(Connection Connection) {
        printMessage("Disconnected");
    }
    @Override
    public void onException(Connection Connection, Exception e) {
        printMessage("Exception :" + e);
    }


}
