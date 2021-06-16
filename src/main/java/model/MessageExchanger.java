package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ClientMessage;
import dto.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageExchanger {
    private final List<IMessageListener> messageListeners;
    private final List<Player> players;
    private final boolean[][] fieldOfTheFirstPlayer;
    private final boolean[][] fieldOfTheSecondPlayer;
    private int playersNumber;
    public MessageExchanger() {
        playersNumber = 0;
        messageListeners = new ArrayList<>();
        players = new ArrayList<>();
        this.fieldOfTheFirstPlayer = new boolean[16][16];
        this.fieldOfTheSecondPlayer = new boolean[16][16];
    }

    public static void send(ObjectOutputStream out, Object obj) {
        try {
            out.writeObject(new ObjectMapper().writeValueAsString(obj));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T receive(ObjectInputStream in, Class<T> clazz) {
        T obj = null;
        try {
            obj = new ObjectMapper().readValue((String) in.readObject(), clazz);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void broadcast(Object message) {
        for (IMessageListener listener : messageListeners) {
            listener.send(message);
        }
    }

    public void addMessageListener(IMessageListener listener) {
        messageListeners.add(listener);
    }

    public void addPlayer(Player player) {
        player.setNumber(++playersNumber);
        players.add(player);
    }

    public int getPlayersNumber(){
        return playersNumber;
    }

    public Player getPlayer(int i) {
        return players.get(i - 1);
    }

    public void removeMessageListener(Socket socket) {
        new ArrayList<>(messageListeners).forEach(iMessageListener -> {
            if (iMessageListener.getSocket().equals(socket)) {
                messageListeners.remove(iMessageListener);
            }
        });
    }

    public void removePlayer(Player player) {
        playersNumber--;
        new ArrayList<>(players).forEach(player1 -> {
            if (player.equals(player1)) {
                players.remove(player1);
            }
        });
    }

    public ServerMessage process(ClientMessage clientMessage) {
        if (players.size() == 2) {
            double[] rgba = getRgba(clientMessage);
            String status = determineStatus(clientMessage);
            for (Player player : players) {
                if (status.equals(player.name())) {
                    player.incAccount();
                }
            }
            return new ServerMessage(
                    clientMessage.name(),
                    getPlayerNumber(clientMessage),
                    rgba[0],
                    rgba[1],
                    rgba[2],
                    rgba[3],
                    clientMessage.x(),
                    clientMessage.y(),
                    status,
                    Player.getAccount(players));
        } else {
            return null;
        }
    }

    public int getPlayerNumber(ClientMessage clientMessage){
        for (Player player : players) {
            if (clientMessage.name().equals(player.name())) {
                return player.number();
            }
        }
        return -1;
    }

    private String determineStatus(ClientMessage clientMessage) {
        int x = clientMessage.x() + 4;
        int y = clientMessage.y() + 4;
        boolean[][] field = players.get(0).name().equals(clientMessage.name()) ? fieldOfTheFirstPlayer : fieldOfTheSecondPlayer;
        field[x][y] = true;
        if (isDraw(fieldOfTheFirstPlayer, fieldOfTheSecondPlayer)) {
            clearFields(fieldOfTheFirstPlayer, fieldOfTheSecondPlayer);
            return "Ничья";
        }
        if (isAPlayerWin(field)) {
            clearFields(fieldOfTheFirstPlayer, fieldOfTheSecondPlayer);
            return clientMessage.name();
        }
        return "";
    }

    private void clearFields(boolean[][] field1, boolean[][] field2) {
        for (int i = 4; i < 12; i++) {
            for (int j = 4; j < 12; j++) {
                field1[i][j] = false;
                field2[i][j] = false;
            }
        }
    }

    private boolean isDraw(boolean[][] field1, boolean[][] field2) {
        boolean b = true;
        for (int i = 4; i < 12; i++) {
            for (int j = 4; j < 12; j++) {
                b &= field1[i][j] || field2[i][j];
            }
        }
        return b;
    }

    private boolean isAPlayerWin(boolean[][] field) {
        for (int i = 4; i < 12; i++) {
            for (int j = 4; j < 12; j++) {
                boolean b1 = true, b2 = true, b3 = true, b4 = true, b5 = true, b6 = true, b7 = true, b8 = true;
                for (int p = i; p <= i + 4; p++) {
                    b1 &= field[p][j];
                }
                for (int p = i - 4; p <= i; p++) {
                    b2 &= field[p][j];
                }
                for (int p = j; p <= j + 4; p++) {
                    b3 &= field[i][p];
                }
                for (int p = j - 4; p <= j; p++) {
                    b4 &= field[i][p];
                }
                int p = i, q = j;
                for (; p <= i + 4 && q <= j + 4; p++, q++) {
                    b5 &= field[p][q];
                }
                p = i;
                q = j;
                for (; p <= i + 4 && q >= j - 4; p++, q--) {
                    b6 &= field[p][q];
                }
                p = i;
                q = j;
                for (; p >= i - 4 && q <= j + 4; p--, q++) {
                    b7 &= field[p][q];
                }
                p = i;
                q = j;
                for (; p >= i - 4 && q >= j - 4; p--, q--) {
                    b8 &= field[p][q];
                }
                if (b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8) {
                    return true;
                }
            }
        }
        return false;
    }

    private double[] getRgba(ClientMessage clientMessage) {
        double[] rgba = new double[4];
        players.forEach(player -> {
            if (player.name().equals(clientMessage.name())) {
                rgba[0] = player.red();
                rgba[1] = player.green();
                rgba[2] = player.blue();
                rgba[3] = player.opacity();
            }
        });
        return rgba;
    }
}
