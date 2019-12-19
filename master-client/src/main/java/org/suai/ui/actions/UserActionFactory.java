package org.suai.ui.actions;

import org.suai.net.Client;
import org.suai.net.Message;
import org.suai.ui.AuthDialog;
import org.suai.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UserActionFactory {
    private final MainFrame mainFrame;

    private final Client client;

    private final Action newUserAction;

    private final Action loginAction;

    private final Action changeNameAction;

    private final Action changePasswordAction;

    public UserActionFactory(MainFrame mainFrame, Client client) {
        this.mainFrame = mainFrame;
        this.client = client;

        newUserAction = new NewUserAction();
        loginAction = new LoginAction();
        changeNameAction = new ChangeNameAction();
        changePasswordAction = new ChangePasswordAction();
    }

    public Action getNewUserAction() {
        return newUserAction;
    }

    public Action getLoginAction() {
        return loginAction;
    }

    public Action getChangeNameAction() {
        return changeNameAction;
    }

    public Action getChangePasswordAction() {
        return changePasswordAction;
    }

    private class NewUserAction extends AbstractAction {
        NewUserAction() {
            super("Registration");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AuthDialog authDialog = new AuthDialog(mainFrame);
            client.sendToServer(new Message(6,authDialog.getUserName() + ";" + authDialog.getPassword()));
            Message answer = client.getMessage();
            if (answer.getMessage().equals("OK")) {
                mainFrame.setIsConnected(true);
                client.setUserName(authDialog.getUserName());
                JOptionPane.showMessageDialog(mainFrame, "Registration OK");
            } else if(answer.getMessage().equals("EXISTS")) {
                JOptionPane.showMessageDialog(mainFrame, "Registration EXISTS: a User with that name already exists");
            }
        }
    }

    private class LoginAction extends AbstractAction {
        LoginAction() {
            super("Login");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AuthDialog authDialog = new AuthDialog(mainFrame);
            client.sendToServer(new Message(5,authDialog.getUserName() + ";" + authDialog.getPassword()));
            Message answer = client.getMessage();
            if (answer.getMessage().equals("OK")) {
                mainFrame.setIsConnected(true);
                client.setUserName(authDialog.getUserName());
                JOptionPane.showMessageDialog(mainFrame, "LOGIN OK");
            } else if(answer.getMessage().equals("EXISTS")) {
                JOptionPane.showMessageDialog(mainFrame, "LOGIN EXISTS: User's name/password invalid");
            }
        }
    }

    private class ChangeNameAction extends AbstractAction {
        ChangeNameAction() {
            super("Change name");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mainFrame.getIsConnected()) {
                String name = JOptionPane.showInputDialog(mainFrame, "Введите новое имя");
                client.sendToServer(new Message(7, name));
                Message answer = client.getMessage();
                if (answer.getMessage().equals("OK")) {
                    client.setUserName(name);
                    JOptionPane.showMessageDialog(mainFrame, "CHANGE NAME OK");
                } else if(answer.getMessage().equals("EXISTS")) {
                    JOptionPane.showMessageDialog(mainFrame, "CHANGE NAME EXISTS: User's name already exists");
                }
            } else  {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }

    // TODO: 16.12.2019 Безопасная передача пароля
    private class ChangePasswordAction extends AbstractAction {
        ChangePasswordAction() {
            super("Change password");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mainFrame.getIsConnected()) {
                String password = JOptionPane.showInputDialog(mainFrame, "Введите новое пароль");
                client.sendToServer(new Message(8, password));
                Message answer = client.getMessage();
                if (answer.getMessage().equals("OK")) {
                    JOptionPane.showMessageDialog(mainFrame, "CHANGE PASSWORD OK");
                } else if(answer.getMessage().equals("EXISTS")) {
                    JOptionPane.showMessageDialog(mainFrame, "CHANGE PASSWORD EXISTS: Something went wrong");
                }
            } else  {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }
}

