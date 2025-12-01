package patterns;

import entities.GameObject;
import entities.Player;
import patterns.MovementStrategy;

public class FollowPlayerMovement implements MovementStrategy {

    private Player player;

    public FollowPlayerMovement(Player player) {
        this.player = player;
    }

    @Override
    public void move(GameObject obj) {
        double dx = player.getX() - obj.getX();
        double dy = player.getY() - obj.getY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        obj.setAngle((float) angle);

        obj.setX(obj.getX() + Math.cos(Math.toRadians(angle)) * obj.getSpeed());
        obj.setY(obj.getY() + Math.sin(Math.toRadians(angle)) * obj.getSpeed());
    }
}
