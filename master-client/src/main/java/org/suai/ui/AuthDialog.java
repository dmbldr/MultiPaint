package org.suai.ui;

import org.suai.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthDialog extends JDialog {

    // TODO: 16.12.2019 Безопасная передача пароля

    JTextField loginField;
    JPasswordField passwordField;

    private String userName;
    private String password;

    public AuthDialog(MainFrame mainFrame) {
        super(mainFrame, "Authorization", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        /*******
         * NAME
         ********/
        Box box1 = Box.createHorizontalBox();
        JLabel loginLabel = new JLabel("Логин:");
        loginField = new JTextField(15);
        box1.add(loginLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(loginField);
        /*******
         * PASSWORD
         ********/
        Box box2 = Box.createHorizontalBox();
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordField = new JPasswordField(15);
        box2.add(passwordLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(passwordField);
        /*******
         * OK CANCEL
         ********/
        Box box3 = Box.createHorizontalBox();
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                userName = loginField.getText();
                password = passwordField.getText();
                dispose();
            }
        });
        JButton cancel = new JButton("Отмена");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
        box3.add(Box.createHorizontalGlue());
        box3.add(ok);
        box3.add(Box.createHorizontalStrut(12));
        box3.add(cancel);

        /*******
         * LAYOUT
         ********/
        loginLabel.setPreferredSize(passwordLabel.getPreferredSize());
        Box mainBox = Box.createVerticalBox();
        mainBox.setBorder(new EmptyBorder(12,12,12,12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(box3);

        setContentPane(mainBox);
        pack();

        setResizable(true);
        setVisible(true);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
