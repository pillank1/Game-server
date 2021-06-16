package model;

import java.net.Socket;

public interface IMessageListener {
    void send(Object message);

    Socket getSocket();
}
