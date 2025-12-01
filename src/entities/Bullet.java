package entities;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

public class Bullet implements Cloneable {

    private double x;
    private double y;
    private float angle;
    private double size;
    private float speed;
    private final Color color = Color.WHITE;

    public Bullet(double x, double y, float angle, double size, float speed) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.size = size;
        this.speed = speed;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    public boolean check(int width, int height) {
        return !(x <= -size || y < -size || x > width || y > height);
    }

    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();
        g2.setColor(color);
        g2.translate(x, y);
        g2.fill(new Ellipse2D.Double(0, 0, getSize(), getSize()));
        g2.setTransform(old);
    }

    public Shape getShape() {
        return new Ellipse2D.Double(x, y, getSize(), getSize());
    }

    public double getCenterX() {
        return x + getSize() / 2;
    }

    public double getCenterY() {
        return y + getSize() / 2;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setAngle(float angle) { this.angle = angle; }

    @Override
    public Bullet clone() {
        try {
            return (Bullet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
