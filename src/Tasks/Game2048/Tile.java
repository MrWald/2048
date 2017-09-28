package Tasks.Game2048;

import java.awt.geom.RoundRectangle2D;

//Клас клітини з числом, який буде намальовано на ігровому полі
public class Tile extends RoundRectangle2D.Double {
    //Зберігається число клітини
    private int n;
    Tile(double x, double y, double width, double height, double arcw, double arch){
        super(x, y, width, height, arcw, arch);
        n= (Math.random() < 0.9) ? 2 : 4;
    }
    //Метод для збільшення розмірів клітини. Використовується для анімації
    void inc(double inc, double width){
        this.x=(1.024-inc)*0.5*width;
        this.y=(1.02-inc)*0.5*width;
        this.width=inc*width*0.99;
        this.height=inc*width*0.99;
        this.arcwidth=inc*width*0.099;
        this.archeight=inc*width*0.099;
    }
    public int getN(){
        return n;
    }
    public void setN(int n){
        this.n=n;
    }
}
