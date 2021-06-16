package model;

import dto.AuthInfo;

import java.util.List;

public class Player {
    private int account;
    private final AuthInfo authInfo;

    private int number;

    public Player(AuthInfo authInfo) {
        this.authInfo = authInfo;
        account = 0;
    }

    public static String getAccount(List<Player> players) {
        return players.get(0).account + ":" + players.get(1).account;
    }

    public String name() {
        return authInfo.name();
    }

    public double red() {
        return authInfo.red();
    }

    public double green() {
        return authInfo.green();
    }

    public double blue() {
        return authInfo.blue();
    }

    public double opacity() {
        return authInfo.opacity();
    }

    public void incAccount() {
        account++;
    }

    public int number() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
