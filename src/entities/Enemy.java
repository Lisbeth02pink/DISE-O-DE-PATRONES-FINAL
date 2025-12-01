package entities;

import graphics.Drawable;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.ImageIcon;

public class Enemy extends GameObject implements Drawable, Cloneable {

    private static final double ENEMY_SIZE = 50;
    private final Image image;
    private final Area enemyShape;
    private final HP hp;

    public Enemy() {
        this.hp = new HP(20, 20);  
        this.image = new ImageIcon(getClass().getResource("/images/enemy.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, ENEMY_SIZE / 2);
        p.lineTo(15, 10);
        p.lineTo(ENEMY_SIZE - 5, 13);
        p.lineTo(ENEMY_SIZE + 10, ENEMY_SIZE / 2);
        p.lineTo(ENEMY_SIZE - 5, ENEMY_SIZE - 13);
        p.lineTo(15, ENEMY_SIZE - 10);
        enemyShape = new Area(p);
    }

    public boolean updateHP(double damage) {
        hp.setCurrentHp(hp.getCurrentHp() - damage);
        return hp.getCurrentHp() > 0;
    }

    public double getHP() {
        return hp.getCurrentHp();
    }

    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    @Override
    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        g2.drawImage(image, tran, null);
      
        if (hp.getCurrentHp() < hp.getMAX_HP()) {
            g2.setColor(Color.GRAY);
            g2.fillRect(0, -10, (int) ENEMY_SIZE, 2);
            g2.setColor(Color.RED);
            int hpWidth = (int) ((hp.getCurrentHp() / hp.getMAX_HP()) * ENEMY_SIZE);
            g2.fillRect(0, -10, hpWidth, 2);
        }
        g2.setTransform(old);
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), ENEMY_SIZE / 2, ENEMY_SIZE / 2);
        return new Area(afx.createTransformedShape(enemyShape));
    }

    public boolean check(int width, int height) {
        Rectangle bounds = getShape().getBounds();
        return !(x <= -bounds.getWidth() || y < -bounds.getHeight() || x > width || y > height);
    }

    @Override
    public Enemy clone() {
        try {
            Enemy clone = (Enemy) super.clone();
            clone.resetHP();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
