package org.suai.ui;

import org.suai.net.Client;
import org.suai.net.Message;
import org.suai.ui.actions.BoardActionFactory;
import org.suai.ui.actions.EditActionFactory;
import org.suai.ui.actions.UserActionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private final Client client;
    private boolean isConnected = false;

    private final UserActionFactory userActionFactory;
    private final BoardActionFactory boardActionFactory;
    private final EditActionFactory editActionFactory;

    private BoardPanel board;

    public static void main(String[] args) {
        new MainFrame();
    }

    public MainFrame() {
        super("MultiPaint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        client = new Client("localhost", 10001);

        userActionFactory = new UserActionFactory(this, client);
        boardActionFactory = new BoardActionFactory(this,client);
        editActionFactory = new EditActionFactory(this);

        setJMenuBar(createMainMenu());

        setSize(840, 600);
        setResizable(false);
        setVisible(true);
    }

    private JMenuBar createMainMenu() {
        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(createUserMenu());
        mainMenu.add(createBoardMenu());
        mainMenu.add(createEditMenu());
        return mainMenu;
    }

    private JMenu createUserMenu() {
        JMenu userMenu = new JMenu("User");
        userMenu.add(userActionFactory.getNewUserAction());
        userMenu.add(userActionFactory.getLoginAction());
        userMenu.add(userActionFactory.getChangeNameAction());
        userMenu.add(userActionFactory.getChangePasswordAction());
        return userMenu;
    }

    private JMenu createBoardMenu() {
        JMenu boardMenu = new JMenu("Board");
        boardMenu.add(boardActionFactory.getNewBoardAction());
        boardMenu.add(boardActionFactory.getJoinBoardAction());
        return boardMenu;
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(editActionFactory.getChangeBrushSize());
        editMenu.add(editActionFactory.getChangeBrushColor());
        return editMenu;
    }

    public void createBoard(String mode, String name) {
        this.board = new BoardPanel(this, client, mode, name, Color.black, 10);
        add(board);
        repaint();
        revalidate();
    }

    public void repaintBoard() {
        removeBoard();
        String nameBoard = board.getName();
        Color brushColor = board.getBrushColor();
        int brushSize = board.getBrushSize();
        client.sendToServer(new Message(1, board.getName()));
        Message answer = client.getMessage();
        if (answer.getMessage().equals("OK")) {
            this.board = new BoardPanel(this, client, "CONNECT", nameBoard, brushColor, brushSize);
            add(board);
            repaint();
            revalidate();
        }
    }


    public void removeBoard() {
        if(board != null) {
            remove(board);
            repaint();
            revalidate();
        }
    }

    public Client getClient() {
        return client;
    }

    public BoardPanel getBoard() {
        return this.board;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean is) {
        this.isConnected = is;
    }

}
