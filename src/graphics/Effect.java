package graphics;

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class Effect implements Cloneable {

    private final double x;
    private final double y;
    private final double maxDistance;
    private final int maxSize;
    private final Color color;
    private final int totalEffects;
    private final float speed;
    private double currentDistance;
    private ModelBoom[] booms;
    private float alpha = 1f;

    public Effect(double x, double y, int totalEffects, int maxSize, double maxDistance, float speed, Color color) {
        this.x = x;
        this.y = y;
        this.totalEffects = totalEffects;
        this.maxSize = maxSize;
        this.maxDistance = maxDistance;
        this.speed = speed;
        this.color = color;
        createRandom();
    }

    private void createRandom() {
        booms = new ModelBoom[totalEffects];
        float per = 360f / totalEffects;
        Random ran = new Random();
        for (int i = 1; i <= totalEffects; i++) {
            int r = ran.nextInt((int) per) + 1;
            int boomSize = ran.nextInt(maxSize) + 1;
            float angle = i * per + r;
            booms[i - 1] = new ModelBoom(boomSize, angle);
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();
        Composite original = g2.getComposite();
        g2.setColor(color);
        g2.translate(x, y);

        for (ModelBoom b : booms) {
            double bx = Math.cos(Math.toRadians(b.getAngle())) * currentDistance;
            double by = Math.sin(Math.toRadians(b.getAngle())) * currentDistance;
            double size = b.getSize();
            double space = size / 2;

            if (currentDistance >= maxDistance - (maxDistance * 0.7f)) {
                alpha = (float) ((maxDistance - currentDistance) / (maxDistance * 0.7f));
                alpha = Math.max(0f, Math.min(1f, alpha));
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(new Rectangle2D.Double(bx - space, by - space, size, size));
        }

        g2.setComposite(original);
        g2.setTransform(old);
    }

    public void update() {
        currentDistance += speed;
    }

    public boolean check() {
        return currentDistance < maxDistance;
    }

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
