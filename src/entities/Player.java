package entities;

import logic.HpRender;
import graphics.Drawable;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.ImageIcon;

public class Player extends HpRender implements Drawable, Cloneable {

    public static final double PLAYER_SIZE = 64;
    private Image image;
    private Image imageSpeed;
    private Area playerShape;
    private boolean speedUp;
    private boolean alive = true;

    public Player() {
        super(new HP(60, 60));
        this.image = new ImageIcon(getClass().getResource("/images/plane.png")).getImage();
        this.imageSpeed = new ImageIcon(getClass().getResource("/images/plane_speed.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(0, 15);
        p.lineTo(20, 5);
        p.lineTo(PLAYER_SIZE + 15, PLAYER_SIZE / 2);
        p.lineTo(20, PLAYER_SIZE - 5);
        p.lineTo(0, PLAYER_SIZE - 15);
        playerShape = new Area(p);
        this.speed = 0f;
        this.strategy = null;
    }

    @Override
    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        g2.drawImage(speedUp ? imageSpeed : image, tran, null);
        hpRender(g2, getShape(), y);
        g2.setTransform(old);
    }

    public Shape getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        return new Area(afx.createTransformedShape(playerShape));
    }

    public void speedUp() {
        speedUp = true;
        speed = Math.min(speed + 0.01f, 1f);
    }

    public void speedDown() {
        speedUp = false;
        speed = Math.max(speed - 0.003f, 0);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public Player clone() {
        try {
            Player clone = (Player) super.clone();
            clone.resetHP();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
