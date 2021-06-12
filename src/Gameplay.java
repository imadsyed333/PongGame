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
    boolean play = false, end = false;
    int playerY = 240, paddleY = 240, dir = 4, seconds = 0, minutes = 0;
    double ballXdir = -dir, ballYdir = -dir, ballX = 326, ballY = 275;
    int playerScore = 0, botScore = 0;
    int playerCombo = 0, botCombo = 0, comboInt = 3;
    int playerSeconds = 0, botSeconds = 0;
    int paddleYdir = dir;
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
            g2.drawImage(playerImage, 670, playerY, 20, 100, this);

            // bot paddle
            g2.drawImage(botImage, 10, paddleY, 20, 100, this);
    
            // ball
            g2.drawImage(ballImage, (int) ballX, (int) ballY, 30, 30, this);
            
            // halfway line
            Rectangle2D halfLine = new Rectangle2D.Double(getWidth() / 2 - 2, 0, 4, 600);
            g2.setColor(Color.white);
            g2.fill(halfLine);
            
            // time
            g2.setFont(smallFont);
            g2.drawString(format.format(minutes) + ':' + format.format(seconds), 10, 40);
            
            // score
            getScore(g2, playerScore, botScore);
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
            else {
                winMessage = "You LOST!!!";
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
            if (playerY >= 460) {
                playerY = 460;
            }
            if (playerY <= 0) {
                playerY = 0;
            }
        }
    }

    // Runs the game
    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            paddleMovement();
            ballMovement();
            
            if (minutes == 1) {
                play = false;
                end = true;
            }
            repaint();
        }  
    }

    // Handles paddle movement
    public void paddleMovement() {
        if (botCombo == comboInt) {
            paddleYdir = 6;
        }
        else {
            paddleYdir = 4;
        }
        if (ballX > 350 && ballX < 500) {
            paddleSpeedChange(3);
        }
        else if (ballX < 350) {
            paddleSpeedChange(paddleYdir);
        }
        else {
            paddleSpeedChange(1);
        }
    }

    // Handles paddle speed change
    public void paddleSpeedChange(int speed) {
        if (paddleY >= 460 || ballY < paddleY) {
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

        Rectangle ballBox = new Rectangle((int) ballX, (int) ballY, 30, 30);
        Rectangle playerBox = new Rectangle(672, playerY, 20, 100);
        Rectangle botBox = new Rectangle(0, paddleY, 20, 100);

        if (ballBox.intersects(playerBox)) {
            if (ballY == playerY || ballY == playerY + 100) {
                ballYdir = -ballYdir;
            }
            ballSpeedChange(0.005);
            playerCombo++;
        }
        else if (ballBox.intersects(botBox)) {
            if (ballY == paddleY || ballY > paddleY + 100) {
                ballYdir = -ballYdir;
            }
            ballSpeedChange(0.005);
            botCombo++;
        }

        if (ballY <= 0 || ballY >= 540) {
            ballYdir = -ballYdir;
        }
        if (ballX <= -90) {
            playerScore++;
            ballX = 326;
            ballY = 275;
        }
        else if (ballX >= 760) {
            botScore++;
            ballX = 326;
            ballY = 275;
        }
    }

    // Handles ball speed change
    public void ballSpeedChange(double force) {
        ballXdir = -ballXdir;

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
            ballX = 326;
            dir = 4;
            ballY = 275;
            playerScore = 0;
            botScore = 0;
            comboInt = 3;
            playerCombo = 0;
            playerSeconds = 0;
            botCombo = 0;
            botSeconds = 0;
            ballXdir = -dir;
            ballYdir = -dir;
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
