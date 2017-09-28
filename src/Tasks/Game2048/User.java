package Tasks.Game2048;

import java.io.Serializable;

//Клас в якому зберігаються дані користувача
public class User implements Serializable{
    //Ім'я користувача
    private String name;
    //Найкращій рахунок користувача
    private int best;
    User(String name, int best){
        this.name=name;
        this.best=best;
    }
    public String getName() {
        return name;
    }

    int getBest() {
        return best;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setBest(int best) {
        this.best = best;
    }
}
