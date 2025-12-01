package graphics;

import entities.Player;
import entities.Enemy;
import java.util.List;

public class GameMemento {

    public final Player playerSnapshot;
    public final List<Enemy> rocketSnapshots;
    public final int score;

    public GameMemento(Player player, List<Enemy> rockets, int score) {
        this.playerSnapshot = player.clone(); 
        this.rocketSnapshots = rockets.stream()
                .map(Enemy::clone)
                .toList(); 
        this.score = score;
    }
}
