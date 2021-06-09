import javax.swing.JFrame;

public class Pong {
    public static void main(String[] args) {
        JFrame game = new JFrame();
        Gameplay mechanics = new Gameplay();
        game.setBounds(0, 0, 700, 600);
        game.setTitle("Rally Pong");
        game.setResizable(true);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.add(mechanics);
        game.setVisible(true);
    }
}
