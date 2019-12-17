package org.suai.ui.actions;

import org.suai.net.Client;
import org.suai.net.Message;
import org.suai.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BoardActionFactory {
    private final MainFrame mainFrame;

    private final Client client;

    private final Action newBoardAction;
    private final Action joinBoardAction;


    public BoardActionFactory(MainFrame mainFrame, Client client) {
        this.mainFrame = mainFrame;
        this.client = client;


        newBoardAction = new newBoardAction();
        joinBoardAction = new joinBoardAction();
    }

    public Action getNewBoardAction() {
        return newBoardAction;
    }

    public Action getJoinBoardAction() {
        return joinBoardAction;
    }


    private class newBoardAction extends AbstractAction {
        newBoardAction() {
            super("New board");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(mainFrame.getIsConnected()) {
                String name = JOptionPane.showInputDialog(mainFrame, "Введите имя доски");
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(mainFrame, "Пустое имя!");
                } else {
                    client.sendToServer(new Message(0, name));
                    Message answer = client.getMessage();
                    if (answer.getMessage().equals("OK")) {
                        JOptionPane.showMessageDialog(mainFrame, name + ": CREATE OK");
                        mainFrame.removeBoard();
                        mainFrame.createBoard("CREATE", name);
                    } else if (answer.getMessage().equals("EXISTS")) {
                        JOptionPane.showMessageDialog(mainFrame, "CREATE EXISTS: a board with that name already exists");
                    }

                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }

    private class joinBoardAction extends AbstractAction {
        joinBoardAction() {
            super("Join board");
        }


        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mainFrame.getIsConnected()) {
                client.sendToServer(new Message(2, ""));
                Message answer = client.getMessage();
                String[] allBoards = answer.getMessage().split(" ");
                String name = (String) JOptionPane.showInputDialog(mainFrame,
                        "Выберите доску, к которой хотите подключитьтся :",
                        "Выбор доски",
                        JOptionPane.QUESTION_MESSAGE,
                        null, allBoards, allBoards[0]);
                client.sendToServer(new Message(1, name));
                answer = client.getMessage();
                if (answer.getMessage().equals("OK")) {
                    JOptionPane.showMessageDialog(mainFrame, name + ": CONNECT OK");
                    mainFrame.removeBoard();
                    mainFrame.createBoard("CONNECT", name);
                } else if (answer.getMessage().equals("NOT FOUND")) {
                    JOptionPane.showMessageDialog(mainFrame, "CONNECT EXISTS: a board with that name not found");
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }

}