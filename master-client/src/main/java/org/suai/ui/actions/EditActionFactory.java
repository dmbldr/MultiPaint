package org.suai.ui.actions;

import org.suai.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditActionFactory {
    private final MainFrame mainFrame;

    private final Action changeBrushSize;
    private final Action changeBrushColor;

    public EditActionFactory(MainFrame mainFrame) {
        this.mainFrame = mainFrame;


        this.changeBrushSize = new ChangeBrushSize();
        this.changeBrushColor = new ChangeBrushColor();
    }

    public Action getChangeBrushSize() {
        return changeBrushSize;
    }

    public Action getChangeBrushColor() {
        return changeBrushColor;
    }

    private class ChangeBrushSize extends AbstractAction {

        ChangeBrushSize() {
            super("Brush size");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mainFrame.getIsConnected()) {
                String[] sizes = {"5", "7", "10", "13", "17", "20", "25", "30", "40", "50"};

                String size = (String) JOptionPane.showInputDialog(mainFrame,
                        "Выберите размер кисти",
                        "Выбор размера", JOptionPane.QUESTION_MESSAGE,
                        null, sizes, sizes[0]);

                mainFrame.getBoard().setBrushSize(Integer.parseInt(size));
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }

    private class ChangeBrushColor extends AbstractAction {
        ChangeBrushColor() {
            super("Brush color");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(mainFrame.getIsConnected()) {
                Color color = JColorChooser.showDialog(mainFrame, "Выбор цвета", null);
                mainFrame.getBoard().setBrushColor(color);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Вы не вошли!");
            }
        }
    }

}
