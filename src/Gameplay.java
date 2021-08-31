import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.swing.*;
import java.awt.*;

public class Gameplay extends JPanel implements KeyListener, ActionListener, MouseMotionListener {
    /**
     *
     */
    // Variable Declarations
    private static final long serialVersionUID = 1L;
    boolean play, end;
    int playerY = 240, paddleY = 240, dir = (int) getHeight() / 150, seconds = 0, minutes = 0;
    double ballXdir = -dir, ballYdir = -dir, ballX = getWidth() / 2 - 30, ballY = getHeight() / 2 - 30;
    int playerScore = 0, botScore = 0;
    int playerCombo = 0, botCombo = 0, comboInt = 3;
    int playerSeconds = 0, botSeconds = 0;
    int paddleYdir = dir;
    int paddleHeight = (int) getHeight() / 6, paddleWidth = (int) getWidth() / 35;
    int ballDim = (int) getHeight() / 20;
    Timer timer = new Timer(5, this);
    DecimalFormat format = new DecimalFormat("00");
    DecimalFormat format2 = new DecimalFormat("0");
    Image playerImage, botImage, ballImage, backgroundImage;
    Font bigFont = new Font("calibri", Font.BOLD, 70);
    Font smallFont = new Font("serif", Font.BOLD, 40);
    Font messageFont = new Font("calibri", Font.BOLD, 30);

    ActionListener actionListener = ae -> {
        if (play) {
            // Keeps track of time in game
            seconds++;
            if (seconds == 60) {
                minutes ++;
                seconds = 0;
            }
        }
    };
    Timer counter = new Timer (1000, actionListener);

    public Gameplay() {
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    // Runs the game
    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            paddleSpeedChange();
            ballMovement();
            
            if (minutes == 1) {
                play = false;
                end = true;
            }
            repaint();
        }  
    }

    // Graphics
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        playerImage = Toolkit.getDefaultToolkit().getImage("./src/player.png");
        botImage = Toolkit.getDefaultToolkit().getImage("./src/paddle.png");
        ballImage = Toolkit.getDefaultToolkit().getImage("./src/ball.png");
        backgroundImage = Toolkit.getDefaultToolkit().getImage("./src/swirly background.jpg");

        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        if (play) {
            // player paddle
            g2.drawImage(playerImage, getWidth() - paddleWidth, playerY, paddleWidth, paddleHeight, this);

            // bot paddle
            g2.drawImage(botImage, 0, paddleY, paddleWidth, paddleHeight, this);
    
            // ball
            g2.drawImage(ballImage, (int) ballX, (int) ballY, ballDim, ballDim, this);
            
            // halfway line
            Rectangle2D halfLine = new Rectangle2D.Double(getWidth() / 2 - 2, 0, 4, getHeight());
            g2.setColor(Color.white);
            g2.fill(halfLine);
            
            // time
            g2.setFont(smallFont);
            g2.drawString(format.format(minutes) + ':' + format.format(seconds), 10, 40);
            
            // score
            getScore(g2, playerScore, botScore);

            // player tracking dot
            g2.setColor(Color.cyan);
            Ellipse2D playerDot = new Ellipse2D.Double(getWidth() - ballDim, playerY, 5, 5);
            g2.draw(playerDot);

            // paddle tracking dot
            Ellipse2D paddleDot = new Ellipse2D.Double(10, paddleY, 5, 5);
            g2.draw(paddleDot);

            // ball tracking dot 
            Ellipse2D ballDot = new Ellipse2D.Double(ballX, ballY, 5, 5);
            g2.draw(ballDot);
        }

        if (!play && !end) {
            // title and opening message
            String title = "RALLY PONG";
            String message = "Press ENTER to begin";
            int width;

            g2.setColor(Color.yellow);
            g2.setFont(messageFont);

            width = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, getWidth() / 2 - width / 2, 300);

            g2.setColor(Color.cyan);
            g2.setFont(bigFont);

            width = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, getWidth() / 2 - width / 2, 200);
        }

        if (!play && end) {
            // final score and ending message
            String endMessage = "Press ENTER to play again";
            String winMessage;
            int width;
        
            if (playerScore > botScore) {
                winMessage = "You WIN!!!";
            }
            else if (playerScore < botScore) {
                winMessage = "You LOST!!!";
            }
            else {
                winMessage = "You TIED!!!";
            }

            g2.setColor(Color.WHITE);
            g2.setFont(messageFont);
            width = g2.getFontMetrics().stringWidth(endMessage);
            g2.drawString(endMessage, getWidth() / 2 - width / 2, 300);

            g2.setColor(Color.YELLOW);
            g2.setFont(bigFont);
            width = g2.getFontMetrics().stringWidth(winMessage);
            g2.drawString(winMessage, getWidth() / 2 - width / 2, 200);
        
            getScore(g2, playerScore, botScore);
        }
        timer.start();
        counter.start();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    // Handles player movement through mouse movement
    @Override
    public void mouseMoved(MouseEvent e) {
        if (play) {
            if (playerCombo == comboInt) {
                playerY = e.getY() + 3;
            }
            else {
                playerY = e.getY();
            }
            if (playerY >= getHeight() - paddleHeight) {
                playerY = 460;
            }
            if (playerY <= 0) {
                playerY = 0;
            }
        }
    }

    // Handles paddle 
    public void paddleSpeedChange() {
        if (botCombo == comboInt) {
            paddleYdir = 6;
        }
        else {
            paddleYdir = 4;
        }
        if (ballX > 350 && ballX < 500) {
            paddleMovement(3);
        }
        else if (ballX < 350) {
            paddleMovement(paddleYdir);
        }
        else {
            paddleMovement(1);
        }
    }

    // Handles paddle speed change
    public void paddleMovement(int speed) {
        if (paddleY >= getHeight() - paddleHeight || ballY < paddleY) {
            paddleY -= speed;
        }
        if (paddleY <= 0 || ballY > paddleY){
            paddleY += speed;
        }
    }

    // Handles ball movement
    public void ballMovement() {
        ballX += ballXdir;
        ballY += ballYdir;

        Rectangle ballBox = new Rectangle((int) ballX, (int) ballY, ballDim, ballDim);
        Rectangle playerBox = new Rectangle(getWidth() - paddleWidth, playerY, paddleWidth, paddleHeight);
        Rectangle botBox = new Rectangle(0, paddleY, paddleWidth, paddleHeight);

        if (ballBox.intersects(playerBox)) {
            if (ballX + ballDim > getWidth() - paddleWidth && ballY + ballDim <= playerY) {
                ballYdir = -ballYdir;
            }
            else {
                ballXdir = -ballXdir;
                ballSpeedChange(0.005);
            }
        }

        else if (ballBox.intersects(botBox)) {
            if (ballX < paddleWidth && ballY + ballDim <= paddleY) {
                ballYdir = -ballYdir;
            }
            else {
                ballXdir = -ballXdir;
                ballSpeedChange(0.005);
            }
        }
        if (ballY <= 0 || ballY + ballDim >= getHeight()) {
            ballYdir = -ballYdir;
        }
        if (ballX + (int) ballDim * 2 < 0) {
            playerScore++;
            ballX = getWidth() / 2;
            ballY = getHeight() / 2;
        }
        else if (ballX > getWidth() + ballDim) {
            botScore++;
            ballX = getWidth() / 2;
            ballY = getHeight() / 2;
        }
    }

    // Handles ball speed change
    public void ballSpeedChange(double force) {
        if (ballXdir < 0) {
            ballXdir -= force;
        }
        else {
            ballXdir += force;
        }
        if (ballYdir < 0) {
            ballYdir -= force;
        }
        else {
            ballYdir += force;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Handles the press of the ENTER key
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            play = true;
            end = false;
            minutes = 0;
            seconds = 0;
            ballX = getWidth() / 2;
            dir = 4;
            ballY = getHeight() / 2;
            playerScore = 0;
            botScore = 0;
            comboInt = 3;
            playerCombo = 0;
            playerSeconds = 0;
            playerY = 240;
            paddleY = 240;
            botCombo = 0;
            botSeconds = 0;
            ballXdir = -dir;
            ballYdir = -dir;
            paddleHeight = (int) getHeight() / 6;
            paddleWidth = (int) getWidth() / 35;
            ballDim = (int) getHeight() / 20;
            dir = (int) getHeight() / 150;
        }
        else if (e.getKeyCode() == KeyEvent.VK_P && play && !end) {
            play = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_R && !play && !end) {
            play = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void print(String message) {
        System.out.println(message);
    }
    
    // Draws the score 
    public void getScore(Graphics g, int playerScore, int botScore) {
        int windowMiddle = getWidth() / 2;
        String scorePlayer = Integer.toString(playerScore);
        String scoreBot = Integer.toString(botScore);
        int width;

        g.setFont(smallFont);
        g.drawString(format.format(minutes) + ':' + format.format(seconds), 10, 40);
        
        width = g.getFontMetrics().stringWidth(scoreBot);
        g.drawString(scoreBot, windowMiddle - width - 5, 40);
        g.drawString(scorePlayer, windowMiddle + 5, 40);
    }
}
