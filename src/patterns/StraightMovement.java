package patterns;

import entities.GameObject;

public class StraightMovement implements MovementStrategy {

    @Override
    public void move(GameObject obj) {
        double x = obj.getX();
        double y = obj.getY();
        float angle = obj.getAngle();
        float speed = obj.getSpeed();
        
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
        
        obj.setX(x);
        obj.setY(y);
    }

}
