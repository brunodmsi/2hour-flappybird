package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import sun.java2d.pipe.DrawImage;

public class FlappyBird implements ActionListener, KeyListener, MouseListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 600, HEIGHT = 600;

    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> columns;
    public Random rand;
    public int ticks, yMotion, score;
    public boolean gameOver, started;
    public boolean paused;
    public String audiojump;
    public String audioscore;
    public BufferedImage birdimage;
    public BufferedImage backgroundimage;
    public BufferedImage getstarted;
    public BufferedImage gameoverimage;
    public BufferedImage pausedimg;
    public BufferedImage pressp;
    public BufferedImage columnimage;
    public BufferedImage footer;

    public FlappyBird() throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        this.birdimage = ImageIO.read(Class.class.getResourceAsStream("/bird.png"));
        this.backgroundimage = ImageIO.read(Class.class.getResourceAsStream("/background.png"));
        this.getstarted = ImageIO.read(Class.class.getResourceAsStream("/getstarted.png"));
        this.gameoverimage = ImageIO.read(Class.class.getResourceAsStream("/gameover.png"));
        this.pausedimg = ImageIO.read(Class.class.getResourceAsStream("/paused.png"));
        this.pressp = ImageIO.read(Class.class.getResourceAsStream("/pressp.png"));
        this.columnimage = ImageIO.read(Class.class.getResourceAsStream("/column.png"));
        this.footer = ImageIO.read(Class.class.getResourceAsStream("/footer.png"));
        //JOptionPane.showMessageDialog(renderer, score, audiojump, HEIGHT, new ImageIcon("/pressp.png"));
        audiojump = "jumping.wav";
        audioscore = "scoring.wav";

        JFrame frame = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        rand = new Random();

        frame.add(renderer);
        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Flappy Bird");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();

    }

    public void addColumn(boolean start) {

        int space = 230;
        int width = 100;
        int height = 50 + rand.nextInt(250);

        if (start) {

            columns.add(new Rectangle(WIDTH + width + columns.size() * 250, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 250, 0, width, HEIGHT - height - space));

        } else {

            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));

        }

    }

    public void paintColumn(Graphics g, Rectangle column) {

        g.drawImage(columnimage, column.x, column.y, column.width, column.height, renderer);

    }

    public void jump() throws LineUnavailableException, UnsupportedAudioFileException, IOException {

        AudioInputStream audiojumping = AudioSystem.getAudioInputStream(new File(audiojump).getAbsoluteFile());
        Clip clipjump = AudioSystem.getClip();
        clipjump.open(audiojumping);
        clipjump.start();

        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 45, 40);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;

        }

        if (!started) {

            started = true;

        } else if (!gameOver) {

            if (yMotion > 0) {

                yMotion = 0;

            }

            yMotion -= 8;

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int speed = 8;

        ticks++;

        if (started && !paused) {

            for (int i = 0; i < columns.size(); i++) {

                Rectangle column = columns.get(i);
                column.x -= speed;

            }

            if (ticks % 2 == 0 && yMotion < 15) {

                yMotion += 2;

            }

            for (int i = 0; i < columns.size(); i++) {

                Rectangle column = columns.get(i);

                if (column.x + column.width < 0) {

                    columns.remove(column);

                    if (column.y == 0) {

                        addColumn(false);

                    }

                }

            }

            bird.y += yMotion;

            for (Rectangle column : columns) {

                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 5 && bird.x + bird.width / 2 < column.x + column.width / 2 + 5) {

                    AudioInputStream audioscoring = null;
                    try {
                        audioscoring = AudioSystem.getAudioInputStream(new File(audioscore).getAbsoluteFile());
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Clip clipscore = null;
                    try {
                        clipscore = AudioSystem.getClip();
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        clipscore.open(audioscoring);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    clipscore.start();
                    score++;

                }

                if (column.intersects(bird)) {

                    gameOver = true;

                    if (bird.x <= column.x) {

                        bird.x = column.x - bird.width;

                    } else {

                        if (column.y != 0) {

                            bird.y = column.y - bird.height;

                        } else if (bird.y < column.height) {

                            bird.y = column.height;

                        }

                    }

                }

            }
            System.out.println(bird.y);

            if (bird.y > HEIGHT - 120 || bird.y >= 476) {

                gameOver = true;

            }

            if (bird.y + yMotion >= HEIGHT - 120) {

                bird.y = HEIGHT - 120 - bird.height;

            }

        }

        renderer.repaint();

    }

    public void repaint(Graphics g) {

        g.drawImage(backgroundimage, 0, 0, renderer);

        g.drawImage(footer, 0, HEIGHT - 120, WIDTH, 100, renderer);

        g.drawImage(birdimage, bird.x, bird.y, renderer);

        for (Rectangle column : columns) {

            paintColumn(g, column);

        }

        if (!started) {

            g.drawImage(getstarted, WIDTH / 2 - 50, HEIGHT / 2 - 100, renderer);

        }

        if (gameOver) {

            g.drawImage(gameoverimage, WIDTH / 2 - 50, HEIGHT / 2 - 100, renderer);

        }

        if (paused) {

            g.drawImage(pausedimg, WIDTH / 2 - 50, HEIGHT / 2 - 100, renderer);
            g.drawImage(pressp, WIDTH / 2 - 60, HEIGHT / 2 - 65, renderer);

        }

        g.setColor(Color.white);
        g.setFont(new Font("Montserrat Light", 1, 40));

        if (!gameOver && started) {

            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);

        }

    }

    public static void main(String[] De_Masi) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        flappyBird = new FlappyBird();

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            try {
                jump();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        if (e.getKeyCode() == KeyEvent.VK_P) {

            paused = !paused;

        }    
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            jump();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
