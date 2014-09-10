package eu.mc5zig.stream;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowFollowTask extends BukkitRunnable {

	private Main plugin;
	private Arrow arrow;
	private Player target;

	public ArrowFollowTask(Main plugin, Arrow arrow, Player target) {
		this.plugin = plugin;
		this.arrow = arrow;
		this.target = target;
		runTaskTimer(plugin, 0, 1);
	}

	public void run() {
		arrow.setVelocity(target.getEyeLocation().toVector().subtract(arrow.getLocation().toVector()).multiply(0.3));
		arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 0);
		if (target.getGameMode() == GameMode.CREATIVE || ((Damageable) target).getHealth() == 0.0) {
			cancel();
			plugin.arrows.remove(this);
		}
	}

	public Arrow getArrow() {
		return arrow;
	}

}
