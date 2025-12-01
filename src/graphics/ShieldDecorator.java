package graphics;

import entities.Player;
import graphics.Drawable;
import java.awt.*;

public class ShieldDecorator implements Drawable {

    private final Drawable base;
    private int timeLeft = 300; 

    public ShieldDecorator(Drawable base) {
        this.base = base;
    }

    @Override
    public void draw(Graphics2D g2) {
        base.draw(g2); 

        if (base instanceof Player player) {
            g2.setColor(new Color(0, 255, 255, 100)); 
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval((int) player.getX(), (int) player.getY(), (int) Player.PLAYER_SIZE, (int) Player.PLAYER_SIZE);
        }

        timeLeft--;
    }

    public boolean isExpired() {
        return timeLeft <= 0;
    }

    public Drawable getBase() {
        return base;
    }
}
