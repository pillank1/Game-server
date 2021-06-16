package server;

import model.MessageExchanger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ParallelServer {

    public static final int PORT = 5635;
    private static MessageExchanger messageExchanger;

    public static void main(String[] args) throws IOException {
        messageExchanger = new MessageExchanger();
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                new ParallelServerThread(socket, objectOutputStream, messageExchanger);
            }
        }
    }
}