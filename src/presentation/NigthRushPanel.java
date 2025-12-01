package presentation;

import graphics.Effect;
import entities.Key;
import entities.Player;
import entities.Enemy;
import entities.Bullet;
import graphics.Drawable;
import patterns.FollowPlayerMovement;
import logic.Historial;
import graphics.ShieldDecorator;
import patterns.StraightMovement;
import graphics.GameMemento;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JComponent;

import java.util.List;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import utils.Sound;
import java.awt.AlphaComposite;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.stream.Collectors;
import java.util.Deque;
import java.util.ArrayDeque;
import javax.swing.JOptionPane;

public class NigthRushPanel extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private Thread enemyGeneratorThread;
    private boolean start = true;
    private Key key;
    private int shotTime;

    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    private Sound sound;
    private Player player;
    private List<Bullet> bullets;
    private List<Enemy> enemys;
    private List<Effect> boomEffects;
    private int score = 0;

    private Bullet bulletPrototype;
    private Drawable playerDrawable;

    private GameMemento savedState;
    private final Deque<GameMemento> savedStates = new ArrayDeque<>();

    private float gameOverAlpha = 0f;
    private boolean paused = false;

  
    private static class Deco {
        int x, y, tipo; 
        Deco(int x, int y, int tipo) { this.x = x; this.y = y; this.tipo = tipo; }
    }
    private final List<Deco> decoraciones = new ArrayList<>();
    private final Random random = new Random();
    private final int TILE = 32; 

    public void start() {
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        initGameObjects();
        initKeyboard();
        initBulletThread();

        thread = new Thread(() -> {
            while (start) {
                long startTime = System.nanoTime();

                if (!player.isAlive()) {
                    gameOverAlpha = Math.min(1f, gameOverAlpha + 0.01f);
                }

                drawBackground();
                drawDecorations();   
                drawGame();
                render();
                long time = System.nanoTime() - startTime;
                if (time < TARGET_TIME) {
                    sleep((TARGET_TIME - time) / 1000000);
                }
            }
        });
        thread.start();
    }

    private void initGameObjects() {
        sound = Sound.getInstance();
        player = new Player();
        player.setStrategy(new StraightMovement());
        player.setX(150);
        player.setY(150);

        bullets = new ArrayList<>();
        enemys = new ArrayList<>();
        boomEffects = new ArrayList<>();
        bulletPrototype = new Bullet(0, 0, 0, 5, 3f);

       
        generarDecoraciones(13);

        
        enemyGeneratorThread = new Thread(() -> {
            while (start) {
                while (paused) {
                    sleep(100);
                }
                addEnemy();
                sleep(3000);
            }
        });
        if (enemyGeneratorThread != null && !enemyGeneratorThread.isAlive()) {
            enemyGeneratorThread.start();
        }

        playerDrawable = player;
    }

    private void generarDecoraciones(int cantidad) {
        decoraciones.clear();
        for (int i = 0; i < cantidad; i++) {
            int tipo = 1 + random.nextInt(2);
            int margin = 40;

            int x, y;
            boolean sobrePlayer;
            int attempts = 0;
            do {
                x = margin + random.nextInt(Math.max(1, getWidth() - margin*2));
                y = margin + random.nextInt(Math.max(1, getHeight() - margin*2));
                Rectangle2D decoRect = new Rectangle2D.Double(x, y, decorBoxWidth(tipo), decorBoxHeight(tipo));
                Rectangle2D playerRect = new Rectangle2D.Double(player.getX(), player.getY(), Player.PLAYER_SIZE, Player.PLAYER_SIZE);
                sobrePlayer = decoRect.intersects(playerRect);
                attempts++;
            } while (sobrePlayer && attempts < 50);

            decoraciones.add(new Deco(x, y, tipo));
        }
    }

    private void addEnemy() {
        Random ran = new Random();

        int locationY1 = ran.nextInt(Math.max(1, height - 50)) + 25;
        Enemy enemy1 = new Enemy();
        enemy1.setX(0);
        enemy1.setY(locationY1);
        enemy1.setAngle(0);
        enemy1.setSpeed(0.6f);
        enemy1.setStrategy(new FollowPlayerMovement(player));
        enemys.add(enemy1);

        int locationY2 = ran.nextInt(Math.max(1, height - 50)) + 25;
        Enemy enemy2 = new Enemy();
        enemy2.setX(width);
        enemy2.setY(locationY2);
        enemy2.setAngle(180);
        enemy2.setSpeed(0.6f);
        enemy2.setStrategy(new FollowPlayerMovement(player));
        enemys.add(enemy2);
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    key.setKey_up(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    key.setKey_down(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    key.setKey_l(true);
                }
                if (e.getKeyCode() == KeyEvent.VK_G) {
                    saveGame();
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    loadGame(false, false);
                }
                if (e.getKeyCode() == KeyEvent.VK_F && !player.isAlive()) {
                    loadGame(true, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    key.setKey_left(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    key.setKey_right(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    key.setKey_up(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    key.setKey_down(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    key.setKey_space(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_J) {
                    key.setKey_j(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    key.setKey_enter(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    key.setKey_l(false);
                }
            }
        });

        new Thread(() -> {
            float s = 0.5f;
            while (start) {
                while (paused) {
                    sleep(5);
                }

                if (player.isAlive()) {
                    
                    double prevX = player.getX();
                    double prevY = player.getY();
                    
                    int speed = 2;

                    float angle = player.getAngle();
                    if (key.isKey_left()) {
                        player.setX(player.getX() - speed);
                    }
                    if (key.isKey_right()) {
                        player.setX(player.getX() + speed);
                    }
                    if (key.isKey_up()) {
                        player.setY(player.getY() - speed); 
                    }
                    if (key.isKey_down()) {
                        player.setY(player.getY() + speed); 
                    }
                    
                    if (key.isKey_l()) {
                        if (!(playerDrawable instanceof ShieldDecorator)) {
                            playerDrawable = new ShieldDecorator(player);
                        }
                    }

                    if (key.isKey_j()) {
                        if (shotTime == 0) {
                            Bullet b = bulletPrototype.clone();
                            double offset = Player.PLAYER_SIZE / 2.0;
                            double bulletX = player.getX() + offset + Math.cos(Math.toRadians(player.getAngle())) * offset;
                            double bulletY = player.getY() + offset + Math.sin(Math.toRadians(player.getAngle())) * offset;

                            b.setX(bulletX);
                            b.setY(bulletY);
                            b.setAngle(player.getAngle());
                            bullets.add(b);
                            sound.soundShoot();
                        }
                        shotTime++;
                        if (shotTime == 15) {
                            shotTime = 0;
                        }
                    } else {
                        shotTime = 0;
                    }

                    if (key.isKey_space()) {
                        player.speedUp();
                    } else {
                        player.speedDown();
                    }

                    player.update();
                    player.setAngle(angle);

                    
                    if (player.getY() < -Player.PLAYER_SIZE) {
                        player.setY(height);
                    } else if (player.getY() > height) {
                        player.setY(-Player.PLAYER_SIZE);
                    }
                    if (player.getX() < -Player.PLAYER_SIZE) {
                        player.setX(width);
                    } else if (player.getX() > width) {
                        player.setX(-Player.PLAYER_SIZE);
                    }

                    
                    if (isCollidingWithDecor(player.getShape())) {
                        player.setX(prevX);
                        player.setY(prevY);
                    }
                    

                } else if (key.isKey_enter()) {
                    resetGame();
                }

                for (Enemy enemy : new ArrayList<>(enemys)) {
                    enemy.update();
                    if (!enemy.check(width, height)) {
                        enemys.remove(enemy);
                    } else if (player.isAlive()) {
                        checkPlayer(enemy);
                    }
                }
                sleep(5);
            }
        }).start();
    }

    private void initBulletThread() {
        new Thread(() -> {
            while (start) {
                while (paused) {
                    sleep(5);
                }

                for (Bullet bullet : new ArrayList<>(bullets)) {
                    bullet.update();
                    checkBullets(bullet);
                    if (!bullet.check(width, height)) {
                        bullets.remove(bullet);
                    }
                }

                for (Effect boomEffect : new ArrayList<>(boomEffects)) {
                    boomEffect.update();
                    if (!boomEffect.check()) {
                        boomEffects.remove(boomEffect);
                    }
                }

                sleep(1);
            }
        }).start();
    }

    private boolean isCollidingWithDecor(java.awt.Shape playerShape) {
        for (Deco d : decoraciones) {
            Rectangle2D rect = new Rectangle2D.Double(d.x, d.y, decorBoxWidth(d.tipo), decorBoxHeight(d.tipo));
            Area area = new Area(playerShape);
            area.intersect(new Area(rect));
            if (!area.isEmpty()) return true;
        }
        return false;
    }

    private int decorBoxWidth(int tipo) {
        return switch (tipo) {
            case 1 -> 45; 
            case 2 -> 80; 
            default -> 0; 
        };
    }

    private int decorBoxHeight(int tipo) {
        return switch (tipo) {
            case 1 -> 35; 
            case 2 -> 30; 
            default -> 0; 
        };
    }

    private void checkBullets(Bullet bullet) {
        // balas vs rockets (igual que antes)
        for (Enemy enemy : new ArrayList<>(enemys)) {
            Area area = new Area(bullet.getShape());
            area.intersect(enemy.getShape());
            if (!area.isEmpty()) {
                boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                if (!enemy.updateHP(bullet.getSize())) {
                    score++;
                    enemys.remove(enemy);
                    sound.soundDestroy();
                    double x = enemy.getX() + 25;
                    double y = enemy.getY() + 25;
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, Color.CYAN));
                } else {
                    sound.soundHit();
                }
                bullets.remove(bullet);
                break;
            }
        }
    }

    private void checkPlayer(Enemy enemy) {
        Area area = new Area(player.getShape());
        area.intersect(enemy.getShape());
        if (!area.isEmpty()) {
            double rocketHp = enemy.getHP();
            if (!enemy.updateHP(player.getHP())) {
                enemys.remove(enemy);
                sound.soundDestroy();
            }
            if (playerDrawable instanceof ShieldDecorator sd && !sd.isExpired()) {
              
            } else {
                if (!player.updateHP(rocketHp)) {
                    player.setAlive(false);
                    Historial.guardarHistorial(score);
                    sound.soundDestroy();
                }
            }
        }
    }

    private void resetGame() {
        score = 0;
        enemys.clear();
        bullets.clear();
        player.setAlive(true);
        player.resetHP();
        player.setX(150);
        player.setY(150);
        player.setAngle(0);
        gameOverAlpha = 0f;

       
        generarDecoraciones(15);
    }

    private final List<Point> stars = new ArrayList<>();

    private void drawBackground() {
       
        g2.setColor(new Color(5, 5, 30));
        g2.fillRect(0, 0, width, height);

       
        if (stars.isEmpty()) {
            Random rnd = new Random();
            for (int i = 0; i < 150; i++) {
                stars.add(new Point(rnd.nextInt(width), rnd.nextInt(height)));
            }
        }

       
        g2.setColor(Color.WHITE);
        for (Point p : stars) {
            g2.fillOval(p.x, p.y, 1 + random.nextInt(2), 1 + random.nextInt(2));
        }
    }
   
    private void drawDecorations() {
        
        if (g2 == null) return;

        for (Deco d : decoraciones) {
            switch (d.tipo) {
                case 1 -> drawRock(d.x, d.y); 
                case 2 -> drawCloud(d.x, d.y);
            }
        }
    }

    private void drawRock(int x, int y) {
        g2.setColor(new Color(110, 110, 110));
        g2.fillRoundRect(x, y + 10, 46, 30, 8, 8);
        g2.setColor(new Color(150, 150, 150));
        g2.fillOval(x + 10, y, 18, 18);
    }

    private void drawCloud(int x, int y) {
        g2.setColor(new Color(220, 220, 255));
        g2.fillOval(x, y, 60, 30);
        g2.setColor(new Color(200, 200, 255));
        g2.fillOval(x + 10, y - 10, 60, 30);
    }

    private void drawGame() {
        if (player.isAlive()) {
            playerDrawable.draw(g2);

            if (playerDrawable instanceof ShieldDecorator sd && sd.isExpired()) {
                playerDrawable = sd.getBase();
            }

            for (Bullet bullet : new ArrayList<>(bullets)) {
                bullet.draw(g2);
            }

            for (Enemy enemy : new ArrayList<>(enemys)) {
                enemy.draw(g2);
            }

            for (Effect boomEffect : new ArrayList<>(boomEffects)) {
                boomEffect.draw(g2);
            }

        } else {
            bullets.clear();
            enemys.clear();
            boomEffects.clear();
        }

        
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString("Score : " + score, 10, 20);

        
        drawHearts();

        if (!player.isAlive()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, gameOverAlpha));

            String text = "GAME OVER";
            String textKey = "Presione ENTER para volver a empezar...";
            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double x = (width - textWidth) / 2;
            double y = height / 2.0;
            g2.setColor(Color.WHITE);
            g2.drawString(text, (int) x, (int) y);

            g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            x = (width - textWidth) / 2;
            g2.drawString(textKey, (int) x, (int) (y + 50));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawHearts() {
        
        int hp = (int) Math.max(0, Math.min(1000, player.getHP())); 
        int maxHp = 60;
        int heartsTotal = (int) Math.ceil(maxHp / 10.0); 
        int fullHearts = (int) Math.floor(hp / 10.0);
        int x0 = 10;
        int y0 = 35;

        for (int i = 0; i < heartsTotal; i++) {
            int x = x0 + i * 28;
            if (i < fullHearts) {
                drawHeartFilled(x, y0);
            } else {
                drawHeartEmpty(x, y0);
            }
        }
    }

    private void drawHeartFilled(int x, int y) {
        g2.setColor(Color.RED);
        g2.fillOval(x, y, 12, 12);
        g2.fillOval(x + 12, y, 12, 12);
        int[] xs = {x, x + 24, x + 12};
        int[] ys = {y + 10, y + 10, y + 26};
        g2.fillPolygon(xs, ys, 3);

        // borde sutil
        g2.setColor(new Color(180, 0, 0));
        g2.drawOval(x, y, 12, 12);
        g2.drawOval(x + 12, y, 12, 12);
    }

    private void drawHeartEmpty(int x, int y) {
        g2.setColor(new Color(80, 80, 80));
        g2.drawOval(x, y, 12, 12);
        g2.drawOval(x + 12, y, 12, 12);
        int[] xs = {x, x + 24, x + 12};
        int[] ys = {y + 10, y + 10, y + 26};
        g2.drawPolygon(xs, ys, 3);
    }
    

    
    private void drawLifeBar() {
        
        double currentHp = player.getHP();
        double maxHp = 60.0;
        double percent = Math.max(0, Math.min(1, currentHp / maxHp));

        int barWidth = 200;
        int barHeight = 15;
        int x = 10;
        int y = 35;

        
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x - 2, y - 2, barWidth + 4, barHeight + 4, 10, 10);

        
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawRoundRect(x - 2, y - 2, barWidth + 4, barHeight + 4, 10, 10);

        
        int filled = (int) (barWidth * percent);
        g2.setColor(new Color(220, 50, 50));
        g2.fillRoundRect(x, y, filled, barHeight, 10, 10);

       
        g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
        g2.setColor(Color.WHITE);
        g2.drawString("Vida: " + (int) currentHp + " / " + (int) maxHp, x + 5, y + barHeight - 3);
    }

    private void render() {
        Graphics g = getGraphics();
        if (g != null && image != null) {
            g.drawImage(image, 0, 0, null);
            g.dispose();
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveGame() {
        savedStates.push(new GameMemento(player, enemys, score));
        paused = true;
    }

    public void loadGame(boolean reiniciarEnCentro, boolean eliminar) {
        if (savedStates.isEmpty()) {
            JOptionPane.showMessageDialog(null, " No hay partida guardada.");
            return;
        }

        GameMemento state = eliminar ? savedStates.pop() : savedStates.peek();

        this.player = state.playerSnapshot.clone();
        this.enemys = state.rocketSnapshots.stream()
                .map(Enemy::clone)
                .collect(Collectors.toCollection(ArrayList::new));
        this.score = state.score;

        for (Enemy enemy : enemys) {
            enemy.setStrategy(new FollowPlayerMovement(player));
        }

        this.player.setAlive(true);
        this.playerDrawable = player;
        this.paused = false;

        if (reiniciarEnCentro) {
            player.setX(getWidth() / 2.0 - Player.PLAYER_SIZE / 2.0);
            player.setY(getHeight() / 2.0 - Player.PLAYER_SIZE / 2.0);
        }

       
        if (enemyGeneratorThread == null || !enemyGeneratorThread.isAlive()) {
            enemyGeneratorThread = new Thread(() -> {
                while (start) {
                    while (paused) sleep(100);
                    addEnemy();
                    sleep(3000);
                }
            });
            enemyGeneratorThread.start();
        }
    }
}
