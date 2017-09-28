package Tasks.Game2048;

import javax.swing.*;
import java.io.File;

//Вікно програми
public class GameWindow extends JFrame {
    //Панелі
    SettingsScreen settingsScreen;
    PlayScreen playScreen;

    public GameWindow(){
        super("2048");
        //Зчитується файл зі збереженими даними користувача
        new File(File.listRoots()[0]+"2048").mkdir();
        User user = UserReader.getUser(new File(File.listRoots()[0] + "2048" + File.separator + "User.dat"));
        //Якщо файлу ще не було створено, створюємо нового користувача
        if(user ==null) user =new User("Player", 0);
        //Створюємо панель налаштувань
        settingsScreen=new SettingsScreen(this, user);
        //Створюємо стартову панель
        StartScreen startScreen = new StartScreen(this, settingsScreen, user, 520, 650);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(startScreen);
        this.pack();
    }
    //Метод після виклику якого поле гри починає реагувати на натискання клавіш
    void initBoard(){
        playScreen.board.setFocusable(true);
        playScreen.board.requestFocusInWindow();
    }
}
