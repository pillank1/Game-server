package server;

import dto.AuthInfo;
import dto.ClientMessage;
import dto.ServerMessage;
import model.IMessageListener;
import model.MessageExchanger;
import model.Player;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ParallelServerThread extends Thread implements IMessageListener {

    private final MessageExchanger messageExchanger;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Player player;
    private final Socket socket;

    public ParallelServerThread(Socket socket, ObjectOutputStream objectOutputStream, MessageExchanger messageExchanger) throws IOException {
        this.messageExchanger = messageExchanger;
        this.out = objectOutputStream;
        this.socket = socket;
        messageExchanger.addMessageListener(this); // подписываюсь на рассылку сообщений от другого игрока
        out.flush();
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        start();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    private boolean isColorInvalid(Player player) {
        return messageExchanger.getPlayer(1).red() == player.red() &&
                messageExchanger.getPlayer(1).green() == player.green() &&
                messageExchanger.getPlayer(1).blue() == player.blue() &&
                messageExchanger.getPlayer(1).opacity() == player.opacity();
    }

    @Override
    public void run() {
        if (messageExchanger.getPlayersNumber() == 1) {
            do {
                player = new Player(MessageExchanger.receive(in, AuthInfo.class));
                if (isColorInvalid(player)) {
                    MessageExchanger.send(out, "Цвет занят");
                } else {
                    MessageExchanger.send(out, "Цвет свободен");
                }
            } while (isColorInvalid(player));
        } else {
            player = new Player(MessageExchanger.receive(in, AuthInfo.class));
            MessageExchanger.send(out, "Цвет свободен");
        }
        messageExchanger.addPlayer(player); // регистрируем игрока
        System.out.println("Игрок " + player.number() + " (" + player.name() + ") присоединился к игре");
        if (player.number() == 1) {
            messageExchanger.broadcast(
                    new ServerMessage(
                            player.name(),
                            player.number(),
                            player.red(),
                            player.blue(),
                            player.green(),
                            player.opacity(),
                            -1,
                            -1,
                            player.name(),
                            ""));
        } else {
            messageExchanger.broadcast(
                    new ServerMessage(
                            player.name(),
                            player.number(),
                            player.red(),
                            player.blue(),
                            player.green(),
                            player.opacity(),
                            -1,
                            -1,
                            messageExchanger.getPlayer(1).name(),
                            ""));
        }
        while (true) {
            ClientMessage clientMessage = MessageExchanger.receive(in, ClientMessage.class);
            if (clientMessage.isCloseRequest()) {
                System.out.println("Игрок " + player.number() + " (" + player.name() + ") покинул игру");
                messageExchanger.removeMessageListener(socket);
                messageExchanger.removePlayer(player);
                messageExchanger.broadcast(
                        new ServerMessage(
                                player.name(),
                                player.number(),
                                player.red(),
                                player.blue(),
                                player.green(),
                                player.opacity(),
                                -1,
                                -1,
                                "Соперник покинул игру",
                                ""));
                try {
                    socket.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ServerMessage serverMessage = messageExchanger.process(clientMessage);
            messageExchanger.broadcast(serverMessage);
        }
    }

    @Override
    public void send(Object message) {
        MessageExchanger.send(out, message);
    }
}