package org.suai.net;

import org.suai.net.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    boolean isConnected = false;
    String serverHost = null;
    int serverPort;
    Socket clientSocket;
    ObjectInputStream readSocket;
    ObjectOutputStream writeSocket;

    ListenFromServer listen;

    public Client(String serverHost, int serverPort) {
        try {
            try {
                this.serverHost = serverHost;
                this.serverPort = serverPort;
                clientSocket = new Socket(serverHost, serverPort);
                writeSocket = new ObjectOutputStream(clientSocket.getOutputStream());
                readSocket = new ObjectInputStream(clientSocket.getInputStream());

                listen = new ListenFromServer();
            } catch (IOException err) {
                System.out.println(err.toString());
                readSocket.close();
                writeSocket.close();
            }
        } catch (IOException err) {
            System.out.println(err.toString());
            err.printStackTrace();
        }
    }

    public synchronized void sendToServer(Message message) {
        try {
            writeSocket.writeObject(message);
            writeSocket.flush();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Message getMessage() {
        return listen.getMessage();
    }


    class ListenFromServer extends Thread {
        private ConcurrentLinkedQueue<Message> answers;

        public ListenFromServer() {
            answers = new ConcurrentLinkedQueue<>();
            this.start();
        }

        public void run() {
            try {
                try {
                    while (true) {
                        synchronized (answers) {
                            answers.add((Message) readSocket.readObject());
                        }
                    }
                } catch (ClassNotFoundException err) {
                    System.out.println(err.toString());
                    err.printStackTrace();
                    readSocket.close();
                    writeSocket.close();
                }
            }
            catch (IOException err) {
                System.out.println(err.getMessage());
                err.printStackTrace();
            }
        }

        public Message getMessage() {
            while(true) {
                if(!answers.isEmpty()) {
                    return answers.remove();
                }
            }
        }
    }
}