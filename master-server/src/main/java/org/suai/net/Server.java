package org.suai.net;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    public static final int PORT = 10001;

    private static final String filename = "/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/main/resources/users.txt";

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
        users = initUsers();
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("PORT: " + serverSocket.getLocalPort());
            while (true) {
                ClientThread newClient = new ClientThread(serverSocket.accept());
                synchronized (clients) {
                    clients.add(newClient);
                    clients.get(clients.size() - 1).start();
                }
                changeDataBase();
            }
        } catch (IOException err) {
            System.out.println(err.getMessage());
        } finally {
            changeDataBase();
        }
    }

    private HashMap<String, String> initUsers() {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String s;
            HashMap<String, String> tmp = new HashMap<>();
            while ((s = br.readLine()) != null) {
                String[] login = s.split(";");
                tmp.put(login[0], login[1]);
            }
            return tmp;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
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
            try (BufferedWriter br = new BufferedWriter(new FileWriter(filename, true))) {
                br.write(name + ";" + password + "\n");
                br.flush();
                users.put(name, password);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
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

    private void changeDataBase() {
        File oldFile = new File(filename);
        File newFile = new File("/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/main/resources/users1.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, false))) {
            for(HashMap.Entry<String, String> it : users.entrySet()) {
                bw.write(it.getKey() + ";" + it.getValue() + "\n");
                bw.flush();
            }
            oldFile.delete();
            newFile.renameTo(oldFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
            synchronized (consoleSync) {
                System.out.println("Клиент подключился");
                synchronized (clients) {
                    System.out.println("Кол-во клиентов: " + clients.size() + "\n");
                }
            }
            try {
                try {
                    while (true) {
                        Message message = (Message) readSocket.readObject();
                        System.out.println(message.getMessage());
                        if(message.getType() == 6) {
                            /***************
                             * REGISTRATION
                             **************/
                            String[] login = message.getMessage().split(";");
                            if(registration(login[0], login[1])) {
                                synchronized (this) {
                                    this.userName = login[0];
                                    writeSocket.writeObject(new Message(6, "OK"));
                                    writeSocket.flush();
                                }
                            }
                            else {
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(6, "EXISTS"));
                                    writeSocket.flush();
                                }
                            }
                        } else if(message.getType() == 5) {
                            /*******
                             * LOGIN
                             *******/
                            String[] split = message.getMessage().split(";");
                            if(login(split[0], split[1])) {
                                synchronized (this) {
                                    this.userName = split[0];
                                    writeSocket.writeObject(new Message(6, "OK"));
                                    writeSocket.flush();
                                }
                            }
                            else {
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(6, "EXISTS"));
                                    writeSocket.flush();
                                }
                            }
                        } else if(message.getType() == 7) {
                            /*************
                             * CHANGE NAME
                             ************/
                            if(users.containsKey(message.getMessage())) {
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(7, "EXISTS"));
                                    writeSocket.flush();
                                }
                            } else {
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
                            changePassword(userName, message.getMessage());
                            synchronized (this) {
                                writeSocket.writeObject(new Message(8, "OK"));
                                writeSocket.flush();
                            }
                        } else if(message.getType() == 2) {
                            /***************
                             * ALL BOARDS
                             **************/
                            StringBuilder allBoard = new StringBuilder();
                            for(HashMap.Entry<String, BufferedImage> entry : boards.entrySet()) {
                                allBoard.append(entry.getKey()).append(" ");
                            }
                            synchronized (this) {
                                writeSocket.writeObject(new Message(2,allBoard.toString()));
                                writeSocket.flush();
                            }
                        } else if (message.getType() == 0) {
                            /***************
                             * CREATE BOARD
                             **************/
                            boolean isContains;
                            synchronized (boards) {
                                isContains = boards.containsKey(message.getMessage());
                            }
                            if (isContains) {
                                synchronized (this) {
                                    writeSocket.writeObject(new Message(0, "EXISTS"));
                                    writeSocket.flush();
                                }
                            } else {
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
                            System.out.println("Клиент недоступен");
                            System.out.println("Кол-во клиентов: " + clients.size());
                        }
                    }
                    //checkBoards(boardName);
                }
            } catch (Exception err) {
                synchronized (consoleSync) {
                    System.out.println(err.toString() + "\n");
                }
            }
        }
    }
}
