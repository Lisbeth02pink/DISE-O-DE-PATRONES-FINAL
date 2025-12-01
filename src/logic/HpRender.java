package logic;

import entities.GameObject;
import entities.HP;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HpRender extends GameObject {

    private final HP hp;

    public HpRender(HP hp) {
        this.hp = hp;
    }

    protected void hpRender(Graphics2D g2, Shape shape, double yOffset) {
        if (hp.getCurrentHp() != hp.getMAX_HP()) {
            double hpY = shape.getBounds().getY() - yOffset - 10;
            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(0, hpY, 50, 2));
            g2.setColor(new Color(253, 91, 91));
            double hpSize = hp.getCurrentHp() / hp.getMAX_HP() * 50;
            g2.fill(new Rectangle2D.Double(0, hpY, hpSize, 2));
        }
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
}
