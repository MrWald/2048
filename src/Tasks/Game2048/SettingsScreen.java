package Tasks.Game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

//Панель завдяки якій користувач може встановлювати рівень складності, змінювати свій нік і роздільну здатність
class SettingsScreen extends JPanel {
    //Кількість клітин на полі
    private int dimension;
    //Час на хід гравця
    private int turn;
    //Число клітини, при якому гравець виграє
    private int max;
    //Вікно програми
    private GameWindow window;
    //Варіанти рівней складності
    private JComboBox<DifficultyLevel> difficulty;
    //Варіанти роздільних здатностей
    private JComboBox<Resolution> resolution;
    //Поле для редагування імені
    private JTextField name;
    //Кнопка для переходу в головне меню
    private JButton exit;
    //Гравець
    private User user;
    SettingsScreen(GameWindow window, User user){
        super();
        this.window=window;
        this.user=user;
        difficulty=new JComboBox<>();
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.ORANGE);
        name=new JTextField();
        name.setText(this.user.getName());
        for(int i = 0; i< DifficultyLevel.lenght; i++)difficulty.addItem(new DifficultyLevel(i));
        difficulty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DifficultyLevel i=(DifficultyLevel)difficulty.getSelectedItem();
                dimension=i.getDimension();
                turn=i.getTurn();
                max=i.getMax();
            }
        });
        //За замовченням обрано перший рівень складності, аби гравець міг запустити гру не заходячи у налаштування
        difficulty.setSelectedIndex(0);
        resolution=new JComboBox<>();
        for(int i=0;i<Resolution.length;i++)resolution.addItem(new Resolution(i));
        resolution.setSelectedIndex(0);
        //Коли гравець натискає на цю кнопку, відбувається збереження його імені в файл і повернення в головне меню
        exit = new JButton("Return");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsScreen.this.user.setName(SettingsScreen.this.name.getText());
                UserReader.saveUser(SettingsScreen.this.user, File.listRoots()[0]+"2048"+File.separator+"User.dat");
                SettingsScreen.this.setVisible(false);
                SettingsScreen.this.window.remove(SettingsScreen.this);
                SettingsScreen.this.window.add(new StartScreen(SettingsScreen.this.window, SettingsScreen.this, SettingsScreen.this.user,
                        ((Resolution)resolution.getSelectedItem()).getWidth(), ((Resolution)resolution.getSelectedItem()).getHeight() ));
                SettingsScreen.this.window.pack();
            }
        });
    }

    //Метод для перестворення панелі при зміні роздільної здатності
    void refresh(){
        this.removeAll();
        GridBagConstraints c=new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.fill=GridBagConstraints.BOTH;
        this.add(new JLabel("Edit name"),c);
        name.setText(this.user.getName());
        c.gridx=1;
        this.add(name, c);
        c.gridx=0;
        c.gridy=1;
        this.add(new JLabel("Difficulty level"), c);
        c.gridx=1;
        this.add(difficulty, c);
        c.gridx=0;
        c.gridy=2;
        this.add(new JLabel("Resolution"), c);
        c.gridx=1;
        this.add(resolution, c);
        //При нажатии на эту кнопку отрисовывается новый вступительный экран и измененный пользователь снова сохраняется в файл
        c.gridx=0;
        c.gridy=3;
        c.gridwidth=2;
        this.add(exit, c);
    }

    int getDimension() {
        return dimension;
    }

    int getTurn() {
        return turn;
    }

    int getMax() {
        return max;
    }

    //Повертає розмір вікна
    public Dimension getPreferredSize() {
        return new Dimension( (int)( ((Resolution)resolution.getSelectedItem()).getWidth() * 0.59615385),
                (int)( ((Resolution)resolution.getSelectedItem()).getHeight() *0.16923077));
    }
}
//Клас з рівнями складності
class DifficultyLevel {
    private static String[] name={"Easy", "Normal", "Insane"};
    private static  int[] dimension={16, 36, 64};
    private static int[] turn={10000, 6000, 3000};
    private static int[] max={2048, 4096, 8192};
    static int lenght=name.length;
    private int i;
    DifficultyLevel(int i){
        this.i=i;
    }
    String getName() {
        return name[i];
    }

    int getDimension() {
        return dimension[i];
    }

    int getTurn() {
        return turn[i];
    }

    int getMax() {
        return max[i];
    }
    public String toString(){
        return name[i]+"("+(int)Math.sqrt(dimension[i])+"x"+(int)Math.sqrt(dimension[i])+", "+turn[i]/1000+" sec, "+max[i]+")";
    }
}
//Клас із роздільними здатностями
class Resolution{
    private static int[] width={520, 800, 1200};
    private static int[] height={650, 1000, 1500};
    static int length=width.length;
    private int i;
    Resolution(int i){
        this.i=i;
    }
    int getWidth() {
        return width[i];
    }

    int getHeight() {
        return height[i];
    }

    public String toString(){
        return width[i]+"x"+height[i];
    }
}
