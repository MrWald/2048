package Tasks.Game2048;

import java.io.*;

//Клас для зчитування/збереження файлу з даними користувача
class UserReader implements Serializable {
    //Метод для зчитування даних з файлу
    static User getUser(File file) {
        try {
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                ObjectInputStream os = new ObjectInputStream(in);
                try {
                    return (User) os.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                os.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Метод для запису даних у файл за вказаним шляхом
    static void saveUser(User user, String path) {
        try {
            if (path.endsWith(File.separator)) path += "User";
            if (!path.contains(".dat")) path += ".dat";
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(user);
            oout.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}