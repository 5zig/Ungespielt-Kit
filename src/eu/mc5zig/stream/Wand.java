package eu.mc5zig.stream;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Wand extends BukkitRunnable {

	private Main plugin;

	private ArrayList<Location> locations = new ArrayList<Location>();

	private final int radius = 50;

	public Wand(Main plugin) {
		this.plugin = plugin;
		for (double i = 0.0; i < 360.0; i += 1.0) {
			double angle = i * Math.PI / 180.0;
			int x = (int) (radius * Math.sin(angle));
			int z = (int) (radius * Math.cos(angle));
			for (int y = 0; y < Bukkit.getWorlds().get(0).getMaxHeight(); y++) {
				locations.add(new Location(Bukkit.getWorlds().get(0), x, y, z));
			}
		}
		runTaskTimer(plugin, 10, 10);
	}

	@Override
	public void run() {
		long started = System.nanoTime();
		for (int i = 0; i < locations.size(); i++) {
			Location location = locations.get(i);
			boolean canSee = false;
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getLocation().distance(location) <= 10.0) canSee = true;
			}
			if (canSee) location.getWorld().playEffect(location, Effect.LAVA_POP, 0);
		}
		long now = System.nanoTime();
		System.out.println(now - started + ", " + locations.size());
	}

}
