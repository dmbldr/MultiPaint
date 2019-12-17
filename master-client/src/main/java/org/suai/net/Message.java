package org.suai.net;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int CREATE = 0;
    public static final int CONNECT = 1;
    public static final int ALLBOARDS = 2;

    public static final int RGB = 3;
    private static final int COLOR = 4;

    private static final int LOGIN = 5;
    private static final int REGISTR = 6;
    private static final int CHANGENAME = 7;
    private static final int CHANGEPASSWORD = 8;

    private int type;
    private String message;
    private int[] rgb;

    public Message(int type, String message) {
        this.message = message;
        this.type = type;
    }

    public Message(int type, int[] rgb) {
        this.type = type;
        this.rgb = rgb;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int[] getRgb() {
        return rgb;
    }
}
