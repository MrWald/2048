package Tasks.Game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Панель яка дозволяє користувачу переглянути детальну інформацію про програму, почати гру або перейти до налаштувань
class StartScreen extends JPanel {
    //Ширина панелі
    private int width;
    //Висота панелі
    private int height;
    //Кнопка для старту програми
    private JButton start;
    //Кнопка для перегляду детальної інформації про програму
    private JButton howPlay;
    //Кнопка для переходу до налаштувань програми
    private JButton settings;
    //Вікно програми
    private GameWindow window;
    //Панель з налаштуваннями
    private SettingsScreen settingsScreen;
    //Гравець
    private User user;
    StartScreen(GameWindow window, final SettingsScreen settingsScreen, User user, final int width, final int height){
        super();
        //Встановлення висоти й ширини панелі
        this.width=(int)(width*0.57692308);
        this.height=(int)(height*0.61538462);
        //Панель в якій буде збережено сукупність кнопок
        final JPanel butPanel = new JPanel();
        this.setLayout(new GridLayout(3,1));
        butPanel.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        this.setBackground(Color.ORANGE);

        this.window = window;
        this.user=user;
        this.settingsScreen=settingsScreen;

        //Назва гри
        JLabel name  = new JLabel("2048");
        name.setFont(new Font("Colibri", Font.PLAIN, 37));
        name.setHorizontalAlignment(JLabel.CENTER);
        name.setForeground(Color.red);
        this.add(name);
        JLabel hello  = new JLabel("Hello, "+this.user.getName());
        hello.setFont(new Font("Colibri", Font.PLAIN, 20));
        hello.setHorizontalAlignment(JLabel.CENTER);

        //Створення кнопки для старту гри
        start=new JButton("Start game");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartScreen.this.window.remove(StartScreen.this);
                StartScreen.this.setVisible(false);
                PlayScreen playScreen=new PlayScreen(StartScreen.this.settingsScreen.getDimension(), StartScreen.this.settingsScreen.getTurn(), StartScreen.this.settingsScreen.getMax(), StartScreen.this.user, StartScreen.this.window, width, height);
                StartScreen.this.window.playScreen=playScreen;
                StartScreen.this.window.add(playScreen);
                StartScreen.this.window.pack();
                StartScreen.this.window.initBoard();
            }
        });
        howPlay=new JButton("About");
        howPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(butPanel, "Program Developers: Fomin Volodymyr,Anastasiia Fil \nVersion 1.0.3 \nUkraine,Kyiv \nKyiv-Mohyla academy \n2017.08.05\nThe game's objective is to slide numbered tiles on a grid to combine them to create a tile with the number 2048.","Info Message",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        settings=new JButton("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartScreen.this.window.remove(StartScreen.this);
                StartScreen.this.setVisible(false);
                StartScreen.this.settingsScreen.setVisible(true);
                StartScreen.this.window.add(StartScreen.this.settingsScreen);
                StartScreen.this.window.pack();
                StartScreen.this.settingsScreen.refresh();
            }
        });
        start.setFont(new Font("Colibri", Font.PLAIN, 18));
        start.setBackground(Color.RED);
        start.setForeground(Color.RED);
        howPlay.setBackground(Color.RED);
        howPlay.setForeground(Color.RED);
        settings.setBackground(Color.RED);
        settings.setForeground(Color.RED);

        howPlay.setFont(new Font("Colibri", Font.PLAIN, 18));
        settings.setFont(new Font("Colibri", Font.PLAIN, 18));
        c.gridx=0;
        c.gridy=0;
        c.weighty=1;
        c.weightx=1;
        c.gridwidth=2;
        c.fill=GridBagConstraints.BOTH;
        butPanel.add(start, c);
        c.gridy=1;
        c.gridwidth=1;
        butPanel.add(howPlay, c);
        c.gridx=1;
        butPanel.add(settings, c);

        this.add(butPanel);
        this.add(hello);

    }

    //Встановлення розмірів панелі
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
}

