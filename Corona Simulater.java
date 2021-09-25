import javax.swing.*;
import java.awt.*;


public class Corona  {
    int length = 400;
    int width = 400;
    int N = 15;
    int T = 2; // 5 generations untill a sick person gets healed;
    int resolution = length / N;
    int rows = length / resolution;
    int cols = width / resolution;
    int Ns = 500;//1
    int Nh = 300; //0
    int Nv = 200;//2
    double pI = 0.25;
    double pV = 0.115;
    int [][] sickRecord;
    int[][] arr;
    int cGeneration = 1, cSick = 0, cVaccinated = 0, cHealthy =0;


    public void makeCanvas() {
        JFrame frame = new JFrame();
        frame.setTitle("Corona");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(width, length);
        arr = firstGeneration();
        while(true) {
            JPanel pn = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    drawOnSurface(arr, g, cGeneration);
                    //System.out.print(cSick + "\n");
                }
            };
            frame.add(pn);
            frame.setVisible(true);
            cGeneration++;
            try {
                 Thread.sleep(500);
            }
            catch (Exception e){
                 System.out.print(e);
            }
            arr = nextGeneration(arr);
        }
    }


    public int getRandomNumber(int min, int max) { // gnerates random  numbers
        return (int) ((Math.random() * (max - min)) + min);

    }

    /**
     * This function makes a 2d arry and fill it with -1.
     * <p>
     *  the function makes a 2d arry and fills it with -1 . since 0 means healthy , 1 means sick and 2 means vaccinated
     *  or healed i used -1 to represent empty places so this way no tow cells sit on the same place .
     * <p>
     * @return  2d arry filled with -1.

     */
    public int[][] make2DArry () {  // make 2d arry and fill it with -1
        int i, j;
        int[][] arr = new int[rows][cols];
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                arr[i][j] = -1;
            }
        }
        return arr;
    }


    public int[][] firstGeneration () {  // make the first generation. fill the arry randomly 0 ,1 ,2.
        int i, j, num;
        sickRecord = new int [rows][cols];
        int [][] arr = make2DArry();
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                num = getRandomNumber(0, 3);
                if (num == 1) // sick
                {
                    sickRecord[i][j] = 1;
                }
                else // not sick
                {
                    sickRecord[i][j] = 0;
                }
                arr[i][j] = num;
            }
        }
       return arr;
    }

    public int[][] nextGeneration(int [][]oldGeneration) {
        int [][] nextGeneration = make2DArry();
        int [][] possibility = possibility();
        int i , j;
        for (i = 0;i < rows; i++)
        {
            for (j = 0; j < cols; j++)
            {
                nextGeneration = update(possibility, nextGeneration , i, j, oldGeneration[i][j]);
                if (nextGeneration[i][j] == 1)//sick
                {
                    sickRecord[i][j] += 1;
                }
            }
        }
        rules(nextGeneration);
        return nextGeneration;
    }

    public  int [][] possibility() {
        int[][] possibility = new int[rows][cols];
        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                possibility[i][j] = 9;
            }
        }
        return possibility;
    }

    public  void updatePossibility(int[][] arr , int x, int y) {
        int i, j, newX, newY;
        for (i = -1; i < 2; i++) {
            for (j = -1; j < 2; j++) {
                {
                    newX = (i + x + rows) % rows;
                    newY = (j + y + cols) % cols;
                    arr[newX][newY] = arr[newX][newY] - 1;
                }
            }
        }
    }

    public  int [][] update(int[][] possibility, int[][] arr ,int x,int y, int value) {
        int i, j, newX, newY, max = 0, bestI = 0, bestJ = 0;
        boolean flag = false;
        for (i = -1; i < 2; i++) {
            for (j = -1; j < 2; j++) {
                  newX = (i + y + rows) % rows;
                  newY = (j + x + cols) % cols;
                if (arr[newX][newY] == -1 && !flag)
                {
                    flag = true;
                    max =  possibility[newX][newY];
                    bestI = newX;
                    bestJ = newY;
                }
                if (flag) {
                    if (possibility[newX][newY] < max) {
                        if (arr[newX][newY] == -1) {
                            max = possibility[newX][newY];
                            bestI = newX;
                            bestJ = newY;
                        }
                    }
                }
            }
        }
        if (flag ) {
            arr[bestI][bestJ] = value;
            updatePossibility(possibility, bestI, bestJ);
        }
        return arr;
    }

    public void rules(int [][] generation) {
        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                if ((generation[i][j] == 0) || (generation[i][j] == 2)) {
                    if (sick(generation, i, j))
                    {
                        generation[i][j] = 1;
                        sickRecord[i][j] = 1;
                    }
                }
                else  if (generation[i][j] == 1)
                {
                    if (sickRecord[i][j] == T) {
                        generation[i][j] = 2; // healed.
                        sickRecord[i][j] = 0;

                    }
                }
            }
        }
    }

    public boolean sick(int [][] generation , int x , int y)
    {
        int l, c = 0;
        int num;
        boolean sick = false;
        int countSick = 0 , i, j, neighborX, neighborY;
        double infectionP = 0;
        for (i = -1; i < 2; i++) {
            for (j = -1; j < 2; j++) {
                neighborX = (i + y + rows) % rows;
                neighborY = (j + x + cols) % cols;
                if (generation[neighborX][neighborY] == 1) {
                    countSick++;
                }
            }
        }
        if (generation[x][y] == 0)
        {
            infectionP = pI;
        }
        else if (generation[x][y] == 2)
        {
            infectionP = pV;
        }
        for (l = 0; l < countSick; l++)
        {
            num = getRandomNumber(1,11);
            if (num < infectionP * 10 + 1)
                sick = true;
            if (sick) {
                c++;
            }
        }
        if (c == countSick) {
            sick = true;
        }
        else {
            sick = false;
        }
        return sick;
    }

  public  void drawOnSurface( int [][] generation , Graphics g,int c) {
      cSick = 0; cVaccinated = 0; cHealthy =0;
       Font myFont = new Font("Courier",Font.BOLD,18);
       g.setFont(myFont);
      // background
      g.setColor(Color.black);
      g.fillRect(0, 0, width, length);
      g.drawRect(0, 0, width, length);
      // notification bar
      g.setColor(Color.gray);
      g.fillRect(0, 0, width, 50);
      g.setColor(Color.black);
      g.drawString("Covid-19",130,20);
      // black line
      g.setColor(Color.black);
      g.fillRect(0, 50, width, 3);
      //font
      myFont = new Font("Courier",Font.BOLD,12);
      g.setFont(myFont);
      // Generation
      g.drawString("Generation:",10,40);
      g.setColor(Color.ORANGE);
      g.drawString(String.valueOf(c),84,40);
      // Healthy
      g.setColor(Color.black);
      g.drawString("Healthy :",105,40);
      g.setColor(Color.yellow);
      g.fillRect(160, 32, 10, 10);
      // Sick
      g.setColor(Color.black);
      g.drawString("Sick :",185,40);
      g.setColor(Color.pink);
      g.fillRect(225, 32, 10, 10);
      // Vaccinated/Healed
      g.setColor(Color.black);
      g.drawString("Vaccinated/Healed :",245,40);
      g.setColor(Color.green);
      g.fillRect(365, 32, 10, 10);
      int i, j , x, y;
      for (i = 0; i < cols; i++) {
          for (j = 0; j < rows; j++) {
              x = i * resolution;
              y = j * resolution + 53;
              if (generation[i][j] == 0)
              {
                  g.setColor(Color.YELLOW); // healthy people - Nh
                  cHealthy++;
              }
              else if (generation[i][j] == 1)
              {
                  g.setColor(Color.pink);  // sick people - Ns
                  cSick++;
              } else if (generation[i][j] == 2)
              {
                  g.setColor(Color.green);   // vacciened people - Nv
                  cVaccinated++;
              }

              g.fillRect(x, y, resolution - 1, resolution -1);
          }

      }
  }

    public static void main(String[] args) {
        Corona corona = new Corona();
        try {
            corona.makeCanvas();
        }
        catch (Exception e){
            System.out.print(e);
        }
    }
  }

