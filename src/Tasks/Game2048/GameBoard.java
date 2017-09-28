package Tasks.Game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

//Ігрове поле
public class GameBoard extends JPanel {
    //Встановлює скільки клітин має бути створено
    private int dimension;
    //Встановлює розміри панелі
    private int size;
    //Встановлює розмір клітини
    private double wid;
    //Свідчить про число клітин
    private int n=1;
    //Свідчить в якому напрямі має бути пересунуто клітини
    private Character direction=null;
    //Свідчить про зайнятість клітини за заданими кординатами
    private boolean[][] tileStatus;
    //Колекція зі створеними клітинами
    private LinkedList<Tile> playerTiles;
    //Колеція з зображеннями на яких буде намальовано клітини
    private LinkedList<BufferedImage> bufferedImages;
    //Свідчить про кілкість очок які набрав гравець
    private int score;
    //Відображає гравцю скільки він набрав очок
    private JLabel current;
    //Відображає гравцю його найкращий рахунок
    private JLabel best;
    //Свідчить, чи було змінено рахунок
    private boolean updated;
    //Зберігає час у який було зроблено останній хід
    private long startTime;
    //Зберігає час, який відведено користувачу на хід
    private int turnTime;
    //Свідчить чи було вичерпано час
    private boolean timeEnd=false;
    //Відображає час який лишився користувачу на хід
    private JLabel time;
    //Таймер для анімації
    private Timer animator = null;
    //Таймер для моніторингу ходу
    private Timer turnCheck=null;
    //Свідчить, чи відбувається зараз анімація
    private boolean animating = false;
    //Відповідає за ступінь анімації
    private double animationС = 0;
    //Колекція з кординатами клітин, які зайняті
    private LinkedList<int[]> takenTiles;
    //Копія колекції з кординатами клітин, які зайняті
    private LinkedList<int[]> takenTilesC;
    //Клітини які були об'єдані з іншими
    private LinkedList<BufferedImage> biToRemove;
    private LinkedList<int[]> tilesToRemoveS;
    private LinkedList<int[]> tilesToRemoveE;
    //Число клітини при отриманні якого гравець виграє
    private int max;

    //Конструктор панелі
    GameBoard(int dimension, int width, JLabel current, JLabel best, JLabel time, int turnTime, int max)
    {
        super();
        score=0;
        this.current=current;
        this.best=best;
        playerTiles=new LinkedList<>();
        bufferedImages= new LinkedList<>();
        takenTiles =new LinkedList<>();
        takenTilesC =new LinkedList<>();
        biToRemove=new LinkedList<>();
        tilesToRemoveS =new LinkedList<>();
        tilesToRemoveE =new LinkedList<>();
        this.dimension=dimension;
        tileStatus =new boolean[ (int)Math.sqrt(dimension) ][( int)Math.sqrt(dimension) ];
        for(int j = 0; j< tileStatus.length; j++) for(int i = 0; i< tileStatus[j].length; i++) tileStatus[j][i]=true;
        this.size =width;
        wid=(0.8/Math.sqrt(dimension))*size;
        this.setBackground(Color.WHITE);
        this.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                moveTiles(e.getKeyCode());
            }
        });
        this.turnTime=turnTime;
        this.time=time;
        this.max=max;
        refreshTime();
        restart();
    }

    //Метод для старту анімації
    private void startAnimation ()
    {
        animating = true;
        animationС = 0;
        animator = new Timer ( 18, new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                if ( animationС <1 )
                {
                    animationС += 0.05;
                    GameBoard.this.repaint ();
                }
                else
                {
                    animating = false;
                    animationС = 0;
                    if (moved() || updated || takenTiles.size()==2) {
                        GameBoard.this.startTime = System.currentTimeMillis();
                    }
                    turnCheck.start();
                    animator.stop ();
                }
            }
        });
        animator.start ();
    }

    //Метод для малювання графічних елементів на панелі
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        //Спочатку малюємо фон
        drawBackground((Graphics2D)g);
        if(animating || timeEnd) {
            //При початку гри ствоюємо тільки дві клітини
            if (n <= 1 && direction == null) {
                if (takenTiles.size()==0) {
                    takenTiles.add(getFreeTile());
                    takenTiles.add(getFreeTile());
                }
                createTile((Graphics2D) g, takenTiles.get(n - 1), n - 1);
                createTile((Graphics2D) g, takenTiles.get(n), n);
            } else {
                switch (direction) {
                    //Якщо ні, то спочатку пересуваємо об'єднані клітини й вже створені
                    case 'l':
                        if (animationС < 1 && !biToRemove.isEmpty()) for (int i = 0; i < biToRemove.size(); i++)
                            g.drawImage(biToRemove.get(i),
                                    (int) ((tilesToRemoveE.get(i)[0] * wid) + (1 - animationС) * ((tilesToRemoveS.get(i)[0] - tilesToRemoveE.get(i)[0]) * wid)),
                                    (int) (tilesToRemoveS.get(i)[1] * wid), null);

                        for (int i = 0; i < bufferedImages.size(); i++)
                            g.drawImage(bufferedImages.get(i),
                                    (int) ((takenTiles.get(i)[0] * wid) + (1 - animationС) * ((takenTilesC.get(i)[0] - takenTiles.get(i)[0]) * wid)),
                                    (int) (takenTiles.get(i)[1] * wid), null);
                        break;
                    case 'r':
                        if (animationС < 1 && !biToRemove.isEmpty()) for (int i = 0; i < biToRemove.size(); i++)
                            g.drawImage(biToRemove.get(i),
                                    (int) ((tilesToRemoveE.get(i)[0] * wid) + (1 - animationС) * ((tilesToRemoveS.get(i)[0] - tilesToRemoveE.get(i)[0]) * wid)),
                                    (int) (tilesToRemoveS.get(i)[1] * wid), null);

                        for (int i = 0; i < bufferedImages.size(); i++)
                            g.drawImage(bufferedImages.get(i),
                                    (int) ((takenTiles.get(i)[0] * wid) + (1 - animationС) * ((takenTilesC.get(i)[0] - takenTiles.get(i)[0]) * wid)),
                                    (int) (takenTiles.get(i)[1] * wid), null);

                        break;
                    case 'u':
                        if (animationС < 1 && !biToRemove.isEmpty()) for (int i = 0; i < biToRemove.size(); i++)
                            g.drawImage(biToRemove.get(i),
                                    (int) (tilesToRemoveE.get(i)[0] * wid),
                                    (int) (tilesToRemoveE.get(i)[1] * wid + (1 - animationС) * ((tilesToRemoveS.get(i)[1] - tilesToRemoveE.get(i)[1]) * wid)), null);

                        for (int i = 0; i < bufferedImages.size(); i++)
                            g.drawImage(bufferedImages.get(i),
                                    (int) (takenTiles.get(i)[0] * wid),
                                    (int) (takenTiles.get(i)[1] * wid + (1 - animationС) * ((takenTilesC.get(i)[1] - takenTiles.get(i)[1]) * wid)), null);

                        break;
                    case 'd':
                        if (animationС < 1 && !biToRemove.isEmpty()) for (int i = 0; i < biToRemove.size(); i++)
                            g.drawImage(biToRemove.get(i),
                                    (int) (tilesToRemoveE.get(i)[0] * wid),
                                    (int) (tilesToRemoveE.get(i)[1] * wid + (1 - animationС) * ((tilesToRemoveS.get(i)[1] - tilesToRemoveE.get(i)[1]) * wid)), null);

                        for (int i = 0; i < bufferedImages.size(); i++)
                            g.drawImage(bufferedImages.get(i),
                                    (int) (takenTiles.get(i)[0] * wid),
                                    (int) (takenTiles.get(i)[1] * wid + (1 - animationС) * ((takenTilesC.get(i)[1] - takenTiles.get(i)[1]) * wid)), null);

                }
                //Якщо клітини було пересунуто або об'єднано, то ще створюємо нову клітину
                if (moved() || updated) {
                    if (animationС <= 0.05) takenTiles.add(getFreeTile());
                    createTile((Graphics2D) g, takenTiles.get(n), n);
                }

            }
            if (win()) {
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int)(0.09*size)));
                g.drawString("You won!", (int) (size*0.2), (int) (size * 0.45));
            } else {
                if (gameOver()) {
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int)(0.09*size)));
                    g.drawString("You lost", (int) (size*0.225), (int) (size * 0.35));
                }
            }
        }
    }

    //Метод для малювання фону клітинок
    private void drawBackground(Graphics2D g)
    {
        double x=Math.sqrt(dimension);
        for(int j=0;j<x;j++)
        {
            for(int i=0;i<x;i++)
            {
                RoundRectangle2D r=new RoundRectangle2D.Double((int)(i*wid),(int)(j*wid),(int)wid,(int)wid,(int)(wid*0.1),(int)(wid*0.1));
                g.setColor(Color.GRAY);
                g.fill(r);
                g.setColor(Color.WHITE);
                g.draw(r);
            }
        }
    }

    //Метод для створення нової клітинки
    private void createTile(Graphics2D g, int[] free, int i)
    {
        if(animating && animationС <=0.05)playerTiles.add(new Tile((int)(0.5 * wid), (int)(0.5 * wid), 0, 0, 0, 0));
        BufferedImage bi = new BufferedImage ( (int)wid, (int)wid, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = bi.createGraphics ();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        playerTiles.get(i).inc(animationС, wid);
        if (playerTiles.get(i).getN() == 2) g2d.setColor(new Color(240,240,240));
        else g2d.setColor(new Color(200,200,200));
        g2d.fill(playerTiles.get(i));
        g2d.setColor(Color.WHITE);
        g2d.draw(playerTiles.get(i));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int)(((4*0.045*size)/Math.sqrt(dimension))* animationС)));
        g2d.drawString("" + playerTiles.get(i).getN(), (int) (wid * 0.43), (int) (wid * 0.55));
        g2d.setComposite ( AlphaComposite.getInstance ( AlphaComposite.SRC_IN ) );
        if(animationС >=1)bufferedImages.add(bi);
        g.drawImage ( bi, (int) (free[0] * wid), (int) (free[1] * wid), null );
    }

    //Метод для перезапуску гри
    void restart()
    {
        if(!animating)
        {
            time.setText((""+(turnTime*0.001)).substring(0,getLast(""+(turnTime*0.001)))+" sec");
            n=1;
            direction=null;
            timeEnd=false;
            turnCheck.stop();
            score=0;
            for (int j = 0; j < tileStatus.length; j++) for (int i = 0; i < tileStatus[j].length; i++) tileStatus[j][i] = true;
            while (!playerTiles.isEmpty()) playerTiles.removeFirst();
            while (!bufferedImages.isEmpty()) bufferedImages.removeFirst();
            while (!takenTiles.isEmpty()) takenTiles.removeFirst();
            while (!takenTilesC.isEmpty()) takenTilesC.removeFirst();
            while (!biToRemove.isEmpty()) biToRemove.removeFirst();
            while (!tilesToRemoveS.isEmpty()) tilesToRemoveS.removeFirst();
            while (!tilesToRemoveE.isEmpty()) tilesToRemoveE.removeFirst();
            startAnimation();
        }
    }
    //Метод для об'єднання, переміщення клітинок й оновлення рахунку гравця в залежності від натиснутої клавіші
    private void moveTiles(int c)
    {
        if(!win() && !gameOver() && !animating)
        {
            switch (c)
            {
                case KeyEvent.VK_LEFT:
                    //Встановлюємо напрям руху клітин
                    direction='l';
                    //Очищення колекцій об'єднаних клітин
                    empty();
                    //Об'єднання клітин
                    uniteTL();
                    //Копіювання стартових позицій клітин які треба пересунути
                    copyPos();
                    //Пересування клітин(встановлення нової позиції)
                    moveFreeTL();
                    //Якщо було об'єднано або пересунуто клітини, то оновлюємо таймер ходу й збільшуємо кількість клітин
                    updated=updated();
                    if(moved() || updated){
                        n++;
                        turnCheck.stop();
                    }
                    //Стартуємо анімацію
                    startAnimation();
                    //Оновлюємо рахунок
                    updateScore();
                    break;
                case KeyEvent.VK_RIGHT:
                    direction='r';
                    empty();
                    uniteTR();
                    copyPos();
                    moveFreeTR();
                    updated=updated();
                    if(moved() || updated){
                        n++;
                        turnCheck.stop();
                    }
                    startAnimation();
                    updateScore();
                    break;
                case KeyEvent.VK_UP:
                    direction='u';
                    empty();
                    uniteTU();
                    copyPos();
                    moveFreeTU();
                    updated=updated();
                    if(moved() || updated){
                        n++;
                        turnCheck.stop();
                    }
                    startAnimation();
                    updateScore();
                    break;
                case KeyEvent.VK_DOWN:
                    direction='d';
                    empty();
                    uniteTD();
                    copyPos();
                    moveFreeTD();
                    updated=updated();
                    if(moved() || updated){
                        n++;
                        turnCheck.stop();
                    }
                    startAnimation();
                    updateScore();
            }
        }
    }

    //Очищення клітин які було об'єднано з іншими
    private void empty()
    {
        while (!biToRemove.isEmpty()) biToRemove.removeFirst();
        while (!tilesToRemoveS.isEmpty()) tilesToRemoveS.removeFirst();
        while (!tilesToRemoveE.isEmpty()) tilesToRemoveE.removeFirst();
    }

    //Метод для отримання вільної клітинки
    private int[] getFreeTile()
    {
        int[] freeT=new int[2];
        int x;
        int y;
        while(true)
        {
            x=(int)(Math.random()*Math.sqrt(dimension));
            y=(int)(Math.random()*Math.sqrt(dimension));
            if(tileStatus[y][x])
            {
                tileStatus[y][x]=false;
                freeT[0]=x;
                freeT[1]=y;
                break;
            }
        }
        return freeT;
    }
    //Метод для об'єднання найближчих клітинок при русі вліво
    private void uniteTL()
    {
        boolean exit;
        for(int j=0;j<Math.sqrt(dimension);j++)
        {
            for(int i1=0;i1<Math.sqrt(dimension);i1++)
            {
                for(int x1 = 0; x1< takenTiles.size(); x1++)
                {
                    if(x1<=n){
                        if(takenTiles.get(x1)[0]==i1 && takenTiles.get(x1)[1]==j)
                        {
                            exit=false;
                            for(int i2=i1;i2<Math.sqrt(dimension);i2++)
                            {
                                for(int x2 = 0; x2< takenTiles.size(); x2++)
                                {
                                    if(x2<=n&&x1!=x2)
                                    {
                                        if(takenTiles.get(x2)[0]==i2 && takenTiles.get(x2)[1]==j)
                                        {
                                            if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN())
                                            {
                                                unite(x1,x2);
                                                if (x1 > x2) x1--;
                                            }
                                            exit=true;
                                            break;
                                        }
                                    }
                                }
                                if(exit)break;
                            }
                        }
                    }
                }
            }
        }
    }
    //Метод для об'єднання найближчих клітинок при русі вправо
    private void uniteTR()
    {
        boolean exit;
        for(int j=0;j<Math.sqrt(dimension);j++)
        {
            for(int i1=(int)Math.sqrt(dimension)-1;i1>=0;i1--)
            {
                for(int x1 = 0; x1< takenTiles.size(); x1++)
                {
                    if(x1<=n){
                        if(takenTiles.get(x1)[0]==i1 && takenTiles.get(x1)[1]==j)
                        {
                            exit=false;
                            for(int i2=i1;i2>=0;i2--){
                                for(int x2 = 0; x2< takenTiles.size(); x2++)
                                {
                                    if(x2<=n&&x1!=x2){
                                        if(takenTiles.get(x2)[0]==i2 && takenTiles.get(x2)[1]==j)
                                        {
                                            if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN())
                                            {
                                                unite(x1,x2);
                                                if (x1 > x2) x1--;
                                            }
                                            exit=true;
                                            break;
                                        }
                                    }
                                }
                                if(exit)break;
                            }
                        }
                    }
                }
            }
        }
    }
    //Метод для об'єднання найближчих клітинок при русі вгору
    private void uniteTU()
    {
        boolean exit;
        for(int i=0;i<Math.sqrt(dimension);i++)
        {
            for(int j1=0;j1<(int)Math.sqrt(dimension);j1++)
            {
                for(int x1 = 0; x1< takenTiles.size(); x1++)
                {
                    if(x1<=n){
                        if(takenTiles.get(x1)[0]==i && takenTiles.get(x1)[1]==j1)
                        {
                            exit=false;
                            for(int j2=j1;j2<(int)Math.sqrt(dimension);j2++)
                            {
                                for(int x2 = 0; x2< takenTiles.size(); x2++){
                                    if(x2<=n&&x1!=x2){
                                        if(takenTiles.get(x2)[0]==i && takenTiles.get(x2)[1]==j2)
                                        {
                                            if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN())
                                            {
                                                unite(x1,x2);
                                                if (x1 > x2) x1--;
                                            }
                                            exit=true;
                                            break;
                                        }
                                    }
                                }
                                if(exit)break;
                            }
                        }
                    }
                }
            }
        }
    }
    //Метод для об'єднання найближчих клітинок при русі вниз
    private void uniteTD()
    {
        boolean exit;
        for(int i=0;i<Math.sqrt(dimension);i++)
        {
            for(int j1=(int)Math.sqrt(dimension)-1;j1>=0;j1--)
            {
                for(int x1 = 0; x1< takenTiles.size(); x1++)
                {
                    if(x1<=n)
                    {
                        if(takenTiles.get(x1)[0]==i && takenTiles.get(x1)[1]==j1)
                        {
                            exit=false;
                            for(int j2=j1;j2>=0;j2--)
                            {
                                for(int x2 = 0; x2< takenTiles.size(); x2++)
                                {
                                    if(x2<=n&&x1!=x2)
                                    {
                                        if(takenTiles.get(x2)[0]==i && takenTiles.get(x2)[1]==j2)
                                        {
                                            if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN())
                                            {
                                                unite(x1,x2);
                                                if (x1 > x2) x1--;
                                            }
                                            exit=true;
                                            break;
                                        }
                                    }
                                }
                                if(exit)break;
                            }
                        }
                    }
                }
            }
        }
    }
    //Метод для об'єднання найближчих клітинок в графічному плані
    private void unite(int x1, int x2)
    {
        playerTiles.get(x1).setN(playerTiles.get(x1).getN() * 2);
        score+=playerTiles.get(x1).getN();
        Graphics2D g = (Graphics2D)bufferedImages.get(x1).getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double c=0.344/Math.sqrt(dimension);
        double r=0.056/Math.sqrt(dimension);
        switch(playerTiles.get(x1).getN())
        {
            case 4:
                g.setColor(new Color(200,200,200));
                break;
            case 8:
                g.setColor(new Color(255,128,0));
                break;
            case 16:
                g.setColor(new Color(255,192,0));
                c-=r;
                break;
            case 32:
                g.setColor(new Color(255,160,0));
                c-=r;
                break;
            case 64:
                g.setColor(new Color(255,0,0));
                c-=r;
                break;
            case 128:
                g.setColor(new Color(255,224,0));
                c-=2*r;
                break;
            case 256:
                g.setColor(new Color(255,255,0));
                c-=2*r;
                break;
            case 512:
                g.setColor(new Color(255,224,0));
                c-=2*r;
                break;
            case 1024:
                g.setColor(new Color(255,224,0));
                c-=3*r;
                break;
            case 2048:
                g.setColor(new Color(255,255,0));
                c-=3*r;
            case 4096:
                g.setColor(new Color(192,0,192));
                c-=3*r;
            case 8192:
                g.setColor(new Color(255,0,255));
                c-=3*r;
        }
        g.fill(playerTiles.get(x1));
        g.setColor(Color.WHITE);
        g.draw(playerTiles.get(x1));
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int)((4*0.045*size)/Math.sqrt(dimension))));
        g.drawString("" + playerTiles.get(x1).getN(), (int) (size * c), (int) (size * (0.44/Math.sqrt(dimension))));
        tileStatus[takenTiles.get(x2)[1]][takenTiles.get(x2)[0]] = true;
        playerTiles.remove(x2);
        biToRemove.add(bufferedImages.get(x2));
        tilesToRemoveS.add(takenTiles.get(x2));
        tilesToRemoveE.add(takenTiles.get(x1));
        bufferedImages.remove(x2);
        takenTiles.remove(x2);
        n--;
    }

    //Копіювання колекції зі значеннями зайнятих кліток
    private void copyPos()
    {
        while (!takenTilesC.isEmpty()) takenTilesC.removeFirst();
        int[] t;
        for(int[] a: takenTiles)
        {
            t=new int[a.length];
            System.arraycopy(a,0,t,0,a.length);
            takenTilesC.add(t);
        }
    }

    //Методи для пересування вже створених кліток в певному напрямку
    private void moveFreeTL()
    {
        int min;
        for(int j=0;j<Math.sqrt(dimension);j++)
        {
            min=getMinX(j);
            for(int i=min;i<Math.sqrt(dimension);i++)
            {
                for (int[] aFreeT : takenTiles)
                {
                    if (aFreeT[1] == j && aFreeT[0] == i)
                    {
                        aFreeT[0] = min;
                        tileStatus[j][i] = true;
                        tileStatus[j][min] = false;
                        min++;
                    }
                }
            }
        }
    }
    private void moveFreeTR()
    {
        int max;
        for(int j=0;j<Math.sqrt(dimension);j++)
        {
            max=getMaxX(j);
            for(int i=max;i>=0;i--)
            {
                for (int[] aFreeT : takenTiles)
                {
                    if (aFreeT[1] == j && aFreeT[0] == i)
                    {
                        aFreeT[0] = max;
                        tileStatus[j][i] = true;
                        tileStatus[j][max] = false;
                        max--;
                    }
                }
            }
        }
    }
    private void moveFreeTU()
    {
        int min;
        for(int i=0;i<Math.sqrt(dimension);i++)
        {
            min=getMinY(i);
            for(int j=min;j<Math.sqrt(dimension);j++)
            {
                for (int[] aFreeT : takenTiles)
                {
                    if (aFreeT[0] == i && aFreeT[1] == j)
                    {
                        aFreeT[1] = min;
                        tileStatus[j][i] = true;
                        tileStatus[min][i] = false;
                        min++;
                    }
                }
            }
        }
    }
    private void moveFreeTD()
    {
        int max;
        for(int i=0;i<Math.sqrt(dimension);i++)
        {
            max=getMaxY(i);
            for(int j=max;j>=0;j--)
            {
                for (int[] aFreeT : takenTiles)
                {
                    if (aFreeT[1] == j && aFreeT[0] == i)
                    {
                        aFreeT[1] = max;
                        tileStatus[j][i] = true;
                        tileStatus[max][i] = false;
                        max--;
                    }
                }
            }
        }
    }

    //Метод для оновлення рахунку гравця
    private void updateScore()
    {
        current.setText(""+score);
        if(Integer.parseInt( best.getText() ) <= score) best.setText( ""+score );

    }

    //Метод, що дозволяє встановити, чи був змінений рахунок
    private boolean updated()
    {
        return Integer.parseInt( current.getText() ) != score;
    }

    //Метод, що дозволяє встановити, чи було пересуното створені клітки
    private boolean moved()
    {
        for(int i = 0; i< takenTilesC.size(); i++)if(takenTiles.get(i)[0]!= takenTilesC.get(i)[0] || takenTiles.get(i)[1]!= takenTilesC.get(i)[1])return true;
        return false;
    }

    //Метод, що дозволяє встановити чи програв гравець
    private boolean gameOver()
    {
        if(timeEnd) return true;
        if(n==dimension-1)
        {
            boolean exit;
            for(int j=0;j<Math.sqrt(dimension);j++)
            {
                for(int i1=0;i1<Math.sqrt(dimension);i1++)
                {
                    for(int x1 = 0; x1< takenTiles.size(); x1++)
                    {
                        if(x1<=n)
                        {
                            if(takenTiles.get(x1)[0]==i1 && takenTiles.get(x1)[1]==j)
                            {
                                exit=false;
                                for(int i2=i1;i2<Math.sqrt(dimension);i2++)
                                {
                                    for(int x2 = 0; x2< takenTiles.size(); x2++)
                                    {
                                        if(x2<=n&&x1!=x2)
                                        {
                                            if(takenTiles.get(x2)[0]==i2 && takenTiles.get(x2)[1]==j)
                                            {
                                                if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN()) return false;
                                                exit=true;
                                                break;
                                            }
                                        }
                                    }
                                    if(exit)break;
                                }
                            }
                        }
                    }
                }
            }
            for(int j=0;j<Math.sqrt(dimension);j++)
            {
                for(int i1=(int)Math.sqrt(dimension)-1;i1>=0;i1--)
                {
                    for(int x1 = 0; x1< takenTiles.size(); x1++)
                    {
                        if(x1<=n)
                        {
                            if(takenTiles.get(x1)[0]==i1 && takenTiles.get(x1)[1]==j)
                            {
                                exit=false;
                                for(int i2=i1;i2>=0;i2--)
                                {
                                    for(int x2 = 0; x2< takenTiles.size(); x2++)
                                    {
                                        if(x2<=n&&x1!=x2)
                                        {
                                            if(takenTiles.get(x2)[0]==i2 && takenTiles.get(x2)[1]==j)
                                            {
                                                if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN()) return false;
                                                exit=true;
                                                break;
                                            }
                                        }
                                    }
                                    if(exit)break;
                                }
                            }
                        }
                    }
                }
            }
            for(int i=0;i<Math.sqrt(dimension);i++)
            {
                for(int j1=0;j1<(int)Math.sqrt(dimension);j1++)
                {
                    for(int x1 = 0; x1< takenTiles.size(); x1++)
                    {
                        if(x1<=n)
                        {
                            if(takenTiles.get(x1)[0]==i && takenTiles.get(x1)[1]==j1)
                            {
                                exit=false;
                                for(int j2=j1;j2<(int)Math.sqrt(dimension);j2++)
                                {
                                    for(int x2 = 0; x2< takenTiles.size(); x2++)
                                    {
                                        if(x2<=n&&x1!=x2)
                                        {
                                            if(takenTiles.get(x2)[0]==i && takenTiles.get(x2)[1]==j2)
                                            {
                                                if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN()) return false;
                                                exit=true;
                                                break;
                                            }
                                        }
                                    }
                                    if(exit)break;
                                }
                            }
                        }
                    }
                }
            }
            for(int i=0;i<Math.sqrt(dimension);i++)
            {
                for(int j1=(int)Math.sqrt(dimension)-1;j1>=0;j1--)
                {
                    for(int x1 = 0; x1< takenTiles.size(); x1++)
                    {
                        if(x1<=n){
                            if(takenTiles.get(x1)[0]==i && takenTiles.get(x1)[1]==j1)
                            {
                                exit=false;
                                for(int j2=j1;j2>=0;j2--)
                                {
                                    for(int x2 = 0; x2< takenTiles.size(); x2++)
                                    {
                                        if(x2<=n&&x1!=x2){
                                            if(takenTiles.get(x2)[0]==i && takenTiles.get(x2)[1]==j2)
                                            {
                                                if(playerTiles.get(x2).getN()==playerTiles.get(x1).getN()) return false;
                                                exit=true;
                                                break;
                                            }
                                        }
                                    }
                                    if(exit)break;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    //Метод, що дозволяє встановити чи виграв гравець
    private boolean win()
    {
        for(Tile a:playerTiles) if(a.getN()==max) return true;
        return false;
    }

    //Метод, що дозволяє встановити максимальну вільну клітінку в заданому стовпчику
    private int getMaxX(int y)
    {
        int max=(int)Math.sqrt(dimension)-1;
        for(int[] a: takenTiles) if(a[1]==y && a[0]==max) max--;
        return max;
    }

    //Метод, що дозволяє встановити мінімальну вільну клітінку в заданому стовпчику
    private int getMinX(int y)
    {
        int min=0;
        for(int[] a: takenTiles) if(a[1]==y && a[0]==min) min++;
        return min;
    }

    //Метод, що дозволяє встановити максимальну вільну клітінку в заданому рядку
    private int getMaxY(int x)
    {
        int max=(int)Math.sqrt(dimension)-1;
        for(int[] a: takenTiles) if(a[0]==x && a[1]==max) max--;
        return max;
    }

    //Метод, що дозволяє встановити мінімальну вільну клітінку в заданому рядку
    private int getMinY(int x)
    {
        int min=0;
        for(int[] a: takenTiles) if(a[0]==x && a[1]==min) min++;
        return min;
    }

    //Метод для оновлення часу який лишився користувачу на хід
    private void refreshTime()
    {
        turnCheck=new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Якщо час закінчився гравець програє
                if (turnTime + startTime <= System.currentTimeMillis() || gameOver() || win()) {
                    timeEnd = true;
                    animationС = 1;
                    copyPos();
                    repaint();
                    turnCheck.stop();
                }
                //Оновлення часу який лишився користувачу на хід
                GameBoard.this.time.setText(("" + Math.abs((turnTime - (System.currentTimeMillis() - startTime))*0.001)).substring(0,getLast("" + Math.abs((turnTime - (System.currentTimeMillis() - startTime))*0.001)))+" sec");
                }
        });
    }
    //Отримання позиції у стрічці з числом де відбувається перехід до десятої частини
    private int getLast(String s)
    {
        int i=0;
        while(s.charAt(i)!='.')i++;
        i+=2;
        return i;
    }
    //Метод який повертає бажані розміри панелі
    public Dimension getPreferredSize()
    {
        return new Dimension(size, size);
    }
}
