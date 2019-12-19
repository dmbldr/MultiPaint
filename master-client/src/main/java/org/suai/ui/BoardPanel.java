package org.suai.ui;

import org.suai.net.Client;
import org.suai.net.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {
    private MainFrame mainFrame;
    private BufferedImage board;
    private String name;
    private Graphics2D graphics;
    private Color brushColor = Color.black;
    int brushSize = 10;

    private final Client client;

    public BoardPanel(MainFrame mainFrame, Client client, String mode, String name, Color brushColor, int brushSize) {
        this.client = client;
        this.mainFrame = mainFrame;
        this.brushColor = brushColor;
        this.brushSize = brushSize;
        board = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        this.name = name;
        if(mode.equals("CREATE")) {
            graphics = board.createGraphics();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, 800, 600);
        } else if(mode.equals("CONNECT")) {
            int[] rgbArray = client.getMessage().getRgb();
            board.setRGB(0, 0, 800, 600, rgbArray, 0, 800);
            graphics = board.createGraphics();
        }

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if(!client.getIsBaned()) {
                    String message = brushColor.getRGB() + " " + (e.getX() - brushSize / 2) + " " + (e.getY() - brushSize / 2) + " " + brushSize;
                    client.sendToServer(new Message(4, message));

                    String[] splitMessage = client.getMessage().getMessage().split(" ", 4);
                    int color = Integer.parseInt(splitMessage[0]);
                    int coordX = Integer.parseInt(splitMessage[1]);
                    int coordY = Integer.parseInt(splitMessage[2]);
                    int size = Integer.parseInt(splitMessage[3]);

                    graphics.setColor(new Color(color));
                    graphics.fillOval(coordX, coordY, size, size);
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(!client.getIsBaned()) {
                    String message = brushColor.getRGB() + " " + (e.getX() - brushSize / 2) + " " + (e.getY() - brushSize / 2) + " " + brushSize;
                    client.sendToServer(new Message(4, message));

                    String[] splitMessage = client.getMessage().getMessage().split(" ", 4);
                    int color = Integer.parseInt(splitMessage[0]);
                    int coordX = Integer.parseInt(splitMessage[1]);
                    int coordY = Integer.parseInt(splitMessage[2]);
                    int size = Integer.parseInt(splitMessage[3]);

                    graphics.setColor(new Color(color));
                    graphics.fillOval(coordX, coordY, size, size);
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mainFrame.repaintBoard();
            }
        });

    }

    public String getName() {
        return name;
    }

    public Color getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(Color color) {
        this.brushColor = color;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(board, 0, 0, this);
    }

    class Draw implements Runnable {
        public void run() {
            while (true) {
                Message s;
                if((s = client.getMessage()) != null) {
                    String[] splitMessage = s.getMessage().split(" ", 4);
                    int color = Integer.parseInt(splitMessage[0]);
                    int coordX = Integer.parseInt(splitMessage[1]);
                    int coordY = Integer.parseInt(splitMessage[2]);
                    int size = Integer.parseInt(splitMessage[3]);

                    graphics.setColor(new Color(color));
                    graphics.fillOval(coordX, coordY, size, size);
                    repaint();
                }
            }
        }
    }
}