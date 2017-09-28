package Tasks.Game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

//Панель з полем гри й іншими статистичними даними
public class PlayScreen extends JPanel {
    //Ширина й висота панелі
    private int width;
    private int height;
    //Ігрове поле
    GameBoard board;
    //Набраний рахунок
    private JLabel score;
    //Найкращий рахунок
    private JLabel best;
    //Панель де зберігаються рахунки користувача
    private JPanel info;
    //Кнопка для виходу з гри до головного меню, після натискання якої також відбувається запис найкращого рахунку користувача в файл
    private JButton exit;
    //Користувач
    private User user;
    //Вікно програми
    private GameWindow window;

    PlayScreen(int dimension, int turn, int max, User user, GameWindow window, int width, int height){
        super();
        this.width=width;
        this.height=height;
        this.setBackground(Color.WHITE);
        this.setLayout(new GridBagLayout());
        this.window=window;
        this.user=user;
        GridBagConstraints c=new GridBagConstraints();
        initInfo();
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(0,(int)(this.width *0.64),(int)(this.height *0.7),0);
        this.add(info,c);
        //Відображення часу який залишився в користувача на те, щоб зробити хід
        JLabel time = new JLabel(("" + (turn * 0.001)).substring(0, getLast("" + (turn * 0.001)))+" sec");
        c.insets=new Insets(0,-(int)(this.width *0.64),(int)(this.height *0.7),0);
        time.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(0.045* this.width)));
        this.add(time,c);
        board=new GameBoard(dimension, this.width, score, best, time, turn, max);
        c.fill=GridBagConstraints.BOTH;
        c.insets=new Insets((int)(this.height *0.2),(int)(this.width *0.1),-(int)(this.height *0.2),-(int)(this.width *0.1));
        this.add(board,c);
    }

    //Метод для отримання позиції у стрічці з числом з десятою частиною
    private int getLast(String s) {
        int i=0;
        while(s.charAt(i)!='.')i++;
        i+=2;
        return i;
    }

    //Метод для додавання панелі зі статистичними даними користувача
    private void initInfo(){
        GridBagConstraints c=new GridBagConstraints();
        info=new JPanel(new GridBagLayout());
        info.setBackground(Color.WHITE);
        c.gridx=0;
        c.gridy=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.insets=new Insets((int)(0.01* height),(int)(0.01* width),0,0);
        info.add(new JLabel("Score"),c);
        c.gridx++;
        c.insets=new Insets((int)(0.01* height),(int)(0.06* width),0,(int)(0.01* width));
        info.add(new JLabel("Best"),c);
        c.gridx--;
        c.gridy++;
        c.insets=new Insets((int)(0.01* height),(int)(0.02* width),0,0);
        score=new JLabel("0");
        info.add(score,c);
        c.gridx++;
        c.insets=new Insets((int)(0.01* height),(int)(0.07* width),0,(int)(0.01* width));
        best=new JLabel(""+user.getBest());
        info.add(best,c);
        c.insets=new Insets((int)(0.05* height),(int)(0.01* width),0,(int)(0.01* width));
        JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.restart();
                board.setFocusable(true);
                board.requestFocusInWindow();
                score.setText("0");
            }
        });
        c.gridx--;
        c.gridy++;
        c.gridwidth=2;
        info.add(newGame,c);
        exit=new JButton("< Return");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayScreen.this.user.setBest(Integer.parseInt(best.getText()));
                UserReader.saveUser(PlayScreen.this.user, File.listRoots()[0]+"2048"+File.separator+"User.dat");
                PlayScreen.this.setVisible(false);
                window.remove(PlayScreen.this);
                PlayScreen.this.window.add(new StartScreen(PlayScreen.this.window, PlayScreen.this.window.settingsScreen, PlayScreen.this.user, PlayScreen.this.width, PlayScreen.this.height));
                PlayScreen.this.window.pack();
            }
        });
        c.gridy=0;
        c.gridx=0;
        c.fill=GridBagConstraints.NONE;
        c.insets=new Insets(0,-(int)(width *0.7),(int)(height *0.9),0);
        this.add(exit, c);

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
}
