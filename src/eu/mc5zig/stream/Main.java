package eu.mc5zig.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {

	public List<ArrowFollowTask> arrows = new ArrayList<ArrowFollowTask>();

	@Override
	public void onEnable() {
		System.out.println("Plugin is an!");
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("ungespielt").setExecutor(this);
		new Wand(this);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ungespielt")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.getInventory().addItem(new ItemStack(Material.IRON_AXE), new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 64));
				player.sendMessage("Du benutzt jetzt das Ungespielt kit!");
			}
		}
		return false;
	}

	@EventHandler
	public void onPlayerBlockPlace(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.TNT) {
			Location loc = event.getBlockPlaced().getLocation();
			event.getBlockPlaced().setType(Material.AIR);
			loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			Iterator<ArrowFollowTask> it = arrows.iterator();
			while (it.hasNext()) {
				ArrowFollowTask task = it.next();
				if (task.getArrow() == arrow) {
					task.cancel();
					it.remove();
					break;
				}
			}
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				player.setVelocity(new Vector(0, 0.5, 0));

				Player nearest = null;
				double distance = 99999;
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					double d = arrow.getLocation().distance(onlinePlayer.getLocation());
					if (d < distance && onlinePlayer != player && onlinePlayer.getGameMode() != GameMode.CREATIVE && ((Damageable) onlinePlayer).getHealth() > 0.0) {
						distance = d;
						nearest = onlinePlayer;
					}
				}
				if (nearest != null && distance != -1) {
					arrows.add(new ArrowFollowTask(this, arrow, nearest));
					player.sendMessage("Focusing " + nearest.getName());
				}
			}
		}
	}

}