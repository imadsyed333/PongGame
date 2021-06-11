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
    Font scoreFont;


    ActionListener actionListener = ae -> {
        if (play) {
            // Keeps track of time in game
            seconds++;
            if (seconds == 60) {
                minutes ++;
                seconds = 0;
            }
            
            // Keeps track of player and bot combo spells
            if (playerCombo == comboInt) {
                playerSeconds++;
                // print("player is in combo " + Integer.toString(playerCombo) + " " + Integer.toString(comboInt));
            }
            if (botCombo == comboInt) {
                botSeconds++;
                // print("bot is in combo " + Integer.toString(botCombo) + " " + Integer.toString(comboInt));
            
            // Makes sure the bot or the player only achieve combo spells for <comboInt> seconds
            if (playerSeconds == comboInt) {}
                playerCombo = 0;
                playerSeconds = 0;
                comboInt++;
                // print("player is not in combo " + Integer.toString(playerCombo) + " " + Integer.toString(comboInt));
            }
            if (botCombo == comboInt) {
                botCombo = 0;
                botSeconds = 0;
                comboInt++;
                // print("bot is not in combo " + Integer.toString(botCombo) + " " + Integer.toString(comboInt));
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
        
        // Rectangle2D background = new Rectangle2D.Double(1, 1, 700, 600);
        // g2.setColor(Color.black);
        // g2.fill(background);

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

            Rectangle2D halfLine = new Rectangle2D.Double(getWidth() / 2 - 2, 0, 4, 600);
            g2.setColor(Color.white);
            g2.fill(halfLine);
            
            g2.setFont(new Font("serif", Font.BOLD, 40));
            g2.drawString(format.format(minutes) + ':' + format.format(seconds), 10, 40);
            
            getScore(g2, playerScore, botScore);
        }

        if (!play && !end) {
            String title = "RALLY PONG";
            String message = "Press ENTER to begin";
            g2.setColor(Color.yellow);
            g2.setFont(new Font("calibri", Font.BOLD, 30));

            int messageWidth = g2.getFontMetrics().stringWidth(message);
            g2.drawString("Press ENTER to begin", getWidth() / 2 - messageWidth / 2, 300);

            g2.setColor(Color.cyan);
            g2.setFont(new Font("calibri", Font.BOLD, 70));

            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString("RALLY PONG", getWidth() / 2 - titleWidth / 2, 200);
        }
        if (!play && end) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("", Font.BOLD, 30));
            g2.drawString("Press ENTER to try again", 200, 300);
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
    
    public void getScore(Graphics g, int playerScore, int botScore) {
        int windowMiddle = getWidth() / 2;
        String scorePlayer = Integer.toString(playerScore);
        String scoreBot = Integer.toString(botScore);
        g.setFont(new Font("serif", Font.BOLD, 40));
        g.drawString(format.format(minutes) + ':' + format.format(seconds), 10, 40);
        
        int bWidth = g.getFontMetrics().stringWidth(scoreBot);
        g.drawString(scoreBot, windowMiddle - bWidth - 5, 40);
        g.drawString(scorePlayer, windowMiddle + 5, 40);
    }
}
