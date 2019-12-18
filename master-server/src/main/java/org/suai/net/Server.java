package org.suai.net;

import org.suai.io.IOFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Server {

    public static final int PORT = 10001;

    private ServerSocket serverSocket = null;
    private HashMap<String, BufferedImage> boards;
    private HashMap<String, String> users;
    private ArrayList<ClientThread> clients;
    private Object consoleSync;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        boards = new HashMap<>();
        clients = new ArrayList<>();
        consoleSync = new Object();
        users = IOFile.initUsers();
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("PORT: " + serverSocket.getLocalPort());
            while (true) {
                ClientThread newClient = new ClientThread(serverSocket.accept());
                synchronized (clients) {
                    clients.add(newClient);
                    clients.get(clients.size() - 1).start();
                }
                IOFile.changeDataBase(users);
            }
        } catch (IOException err) {
            System.out.println(err.getMessage());
        } finally {
            IOFile.changeDataBase(users);
        }
    }

    private boolean login(String name, String password) {
        for(HashMap.Entry<String, String> it : users.entrySet()) {
            if(it.getKey().equals(name)) {
                if(it.getValue().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean registration(String name, String password) {
        if(!users.containsKey(name)) {
            IOFile.registration(name, password);
            users.put(name, password);
            return true;
        } else {
            return false;
        }
    }

    private void changeName(String oldName, String newName) {
        String password = users.get(oldName);
        users.remove(oldName);
        users.put(newName, password);
    }

    private void changePassword(String name, String newPassword) {
        users.remove(name);
        users.put(name, newPassword);
    }

    class ClientThread extends Thread {
        private String userName;
        private boolean isBan;

        private Socket clientSocket;
        private ObjectInputStream readSocket = null;
        private ObjectOutputStream writeSocket = null;
        private String boardName = null;

        Graphics2D graphics = null;

        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                readSocket = new ObjectInputStream(clientSocket.getInputStream());
                writeSocket = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException err) {
                synchronized (consoleSync) {
                    System.out.println(err.getMessage());
                    err.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
            synchronized (consoleSync) {
                Date now = new Date();
                System.out.println(time.format(now) + ":  Клиент подключился");
                synchronized (clients) {
                    System.out.println("Кол-во клиентов: " + clients.size() + "\n");
                }
            }
            try {
                try {
                    while (true) {
                        Message message = (Message) readSocket.readObject();
                        if(message.getType() == 6) {
                            /***************
                             * REGISTRATION
                             **************/
                            Date now = new Date();
                            System.out.print(time.format(now));
                            String[] login = message.getMessage().split(";");
                            if(registration(login[0], login[1])) {
                                System.out.println(": Registr OK: " + login[0]);
                                synchronized (this) {
                                    this.userName = login[0];
                                    writeSocket.writeObject(new Message(6, "OK"));
                                    writeSocket.flush();
                                }
                            }
                            else {
                                System.out.println(": Registr EXISTS: " + login[0]);
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(6, "EXISTS"));
                                    writeSocket.flush();
                                }
                            }
                        } else if(message.getType() == 5) {
                            /*******
                             * LOGIN
                             *******/
                            Date now = new Date();
                            System.out.print(time.format(now));

                            String[] split = message.getMessage().split(";");
                            if(login(split[0], split[1])) {
                                System.out.println(": Login OK: " + split[0]);
                                synchronized (this) {
                                    this.userName = split[0];
                                    writeSocket.writeObject(new Message(6, "OK"));
                                    writeSocket.flush();
                                }
                            }
                            else {
                                System.out.println(": Login EXISTS: " + split[0]);
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(6, "EXISTS"));
                                    writeSocket.flush();
                                }
                            }
                        } else if(message.getType() == 7) {
                            /*************
                             * CHANGE NAME
                             ************/
                            Date now = new Date();
                            System.out.print(time.format(now));

                            if(users.containsKey(message.getMessage())) {
                                System.out.println(": Change " + userName + " to " + message.getMessage() + " EXISTS");
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(7, "EXISTS"));
                                    writeSocket.flush();
                                }
                            } else {
                                System.out.println(": Change " + userName + " to " + message.getMessage() + " OK");
                                changeName(userName, message.getMessage());
                                userName = message.getMessage();
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(7, "OK"));
                                    writeSocket.flush();
                                }
                            }
                        } else if(message.getType() == 8) {
                            /*************
                             * CHANGE PASSWORD
                             ************/
                            Date now = new Date();
                            System.out.print(time.format(now));

                            changePassword(userName, message.getMessage());
                            System.out.println(": Change password " + userName + " OK");
                            synchronized (this) {
                                writeSocket.writeObject(new Message(8, "OK"));
                                writeSocket.flush();
                            }
                        } else if(message.getType() == 2) {
                            /***************
                             * ALL BOARDS
                             **************/
                            Date now = new Date();
                            System.out.print(time.format(now));

                            StringBuilder allBoard = new StringBuilder();
                            for(HashMap.Entry<String, BufferedImage> entry : boards.entrySet()) {
                                allBoard.append(entry.getKey()).append(" ");
                            }
                            System.out.println(": Get all boards name by " + userName + " OK");
                            synchronized (this) {
                                writeSocket.writeObject(new Message(2,allBoard.toString()));
                                writeSocket.flush();
                            }
                        } else if (message.getType() == 0) {
                            /***************
                             * CREATE BOARD
                             **************/
                            Date now = new Date();
                            System.out.print(time.format(now));

                            boolean isContains;
                            synchronized (boards) {
                                isContains = boards.containsKey(message.getMessage());
                            }
                            if (isContains) {
                                System.out.println(": Create board " + message.getMessage() + " by " + userName + " EXISTS");
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(0, "EXISTS"));
                                    writeSocket.flush();
                                }
                            } else {
                                System.out.println(": Create board " + message.getMessage() + " by " + userName + " OK");
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(0,"OK"));
                                    writeSocket.flush();
                                }

                                boardName = message.getMessage();
                                synchronized (boards) {
                                    boards.put(boardName, new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB));
                                    graphics = boards.get(boardName).createGraphics();
                                }
                                synchronized (boards.get(boardName)) {
                                    graphics.setColor(Color.white);
                                    graphics.fillRect(0, 0, 800, 600);
                                }
                                synchronized (consoleSync) {
                                    System.out.println("Доска \"" + boardName + "\" создана");
                                    synchronized (boards) {
                                        System.out.println("Кол-во досок: " + boards.size() + "\n");
                                    }
                                }
                            }
                        } else if (message.getType() == 1) {
                            /***************
                             * CONNECT BOARD
                             **************/
                            boolean isContains;
                            synchronized (boards) {
                                isContains = boards.containsKey(message.getMessage());
                            }
                            if (isContains) {
                                System.out.println(": Connect board " + message.getMessage() + " by " + userName + " OK");
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(1,"OK"));
                                    writeSocket.flush();
                                }

                                boardName = message.getMessage();
                                synchronized (boards.get(boardName)) {
                                    graphics = boards.get(boardName).createGraphics();
                                }
                                int[] rgbArray = new int[480000];
                                synchronized (boards.get(boardName)) {
                                    boards.get(boardName).getRGB(0, 0, 800, 600, rgbArray, 0, 800);
                                }
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(3, rgbArray));
                                    writeSocket.flush();
                                }
                            } else {
                                System.out.println(": Connect board " + message.getMessage() + " by " + userName + " EXISTS");
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(1, "NOT FOUND"));
                                    writeSocket.flush();
                                }
                            }
                        } else if (boardName != null) {
                            /***************
                             * DRAW ON BOARD
                             **************/
                            String[] splitMessage = message.getMessage().split(" ", 4);
                            int color = Integer.parseInt(splitMessage[0]);
                            int coordX = Integer.parseInt(splitMessage[1]);
                            int coordY = Integer.parseInt(splitMessage[2]);
                            int size = Integer.parseInt(splitMessage[3]);
                            synchronized (boards.get(boardName)) {
                                graphics.setColor(new Color(color));
                                graphics.fillOval(coordX, coordY, size, size);
                            }
                            synchronized (this) {
                                writeSocket.writeObject(message);
                                writeSocket.flush();
                            }
                        }
                    }
                } finally {
                    clientSocket.close();
                    readSocket.close();
                    writeSocket.close();
                    synchronized (clients) {
                        clients.remove(this);
                        synchronized (consoleSync) {
                            Date now = new Date();
                            System.out.println(time.format(now) + ": Клиент недоступен");
                            System.out.println("Кол-во клиентов: " + clients.size());
                        }
                    }
                }
            } catch (Exception err) {
                synchronized (consoleSync) {
                    System.out.println(err.toString() + "\n");
                }
            }
        }
    }
}
