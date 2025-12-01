package entities;

import patterns.MovementStrategy;

public abstract class GameObject {

    protected double x, y;
    protected float angle;
    protected float speed;
    protected MovementStrategy strategy;

    public void update() {
        if (strategy != null) {
            strategy.move(this);
        }
    }   

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }
}
