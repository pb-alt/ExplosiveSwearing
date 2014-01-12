package tk.pbfy0.explosiveswearing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Punishments implements Listener{
	public static ExplosiveSwearingWorldGuard wg;
	public static Economy economy;
	public static ExplosiveSwearing plugin;
	private Random rng;
	Map<Player, String> punished;
	public Punishments(ExplosiveSwearing plugini, Economy ei, ExplosiveSwearingWorldGuard wgi){
		economy = ei;
		wg = wgi;
		rng = new Random();
		plugin = plugini;
		punished = new HashMap<Player, String>();
	}
	public Punishments(){
		wg = null;
		economy = null;
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		Player player;
		 if (!(event.getEntity() instanceof Player)){
			 return;
		 }
		 player = (Player)event.getEntity();
		 String dm = punished.get(player);
		 if(dm != null){
			 ((PlayerDeathEvent) event).setDeathMessage(player.getName() + " " + dm);
			 punished.remove(player);
		 }
	}

    void randomlyPunish(Player player, int amount){
    	if(!(plugin.broadcast == null) && !plugin.broadcast.equals("")){
    		plugin.getServer().broadcastMessage(plugin.broadcast.replaceAll("\\[name\\]", Matcher.quoteReplacement(player.getName())));
    	}
    	if(!(plugin.PM == null) && !plugin.PM.equals("")){
    		player.sendMessage(plugin.PM.replaceAll("\\[name\\]", Matcher.quoteReplacement(player.getName())));
    	}
    	double random = rng.nextDouble();
    	double c = 0;
    	if(random < (c += plugin.chances.explode)){
			if(plugin.explodable || plugin.kill){
				punished.put(player, plugin.caps ? "used explosive caps" : "swore explosively");
			}
			this.goBoom(player, plugin.explodable);
    	}else if(random < (c += plugin.chances.lightning)){
    		if(plugin.kill){
    			punished.put(player, "was struck down");
    		}
    		this.lightning(player);
    		if(plugin.kill){
    			player.setHealth(0);
    		}
    	}else if(random < (c += plugin.chances.suffocate)){
    		this.suffocate(player);
    	}else if(random < (c += plugin.chances.voiddrop)){
    		if(plugin.kill){
    			punished.put(player, "was voided");
    		}
    		this.voidDrop(player);
    		if(plugin.kill){
    			player.setHealth(0);
    		}
    	}else if(random < (c += plugin.chances.sky)){
    		this.skyDrop(player);
    	}else if(random < (c += plugin.chances.incinerate)){
    		this.incinerate(player);
    	}else if(random < (c += plugin.chances.starve)){
    		this.starve(player);
    	}else if(random < (c += plugin.chances.fine)){
    		this.fine(player, amount * plugin.fine);
    	}
    }

	public void goBoom(Player player, boolean blockDamage){
		int explosionPower = 0;
		if(blockDamage){
			Location loc = player.getLocation();
			Location p1 = loc.clone().subtract(3, 3, 3);
			Location p2 = loc.clone().add(3, 3, 3);
			explosionPower = (wg == null || wg.canBuildWG(p1, p2, player)) ? 4 : 0;
		}
		player.getWorld().createExplosion(player.getLocation(), explosionPower);
		player.setHealth(0);
	}
	
	public void lightning(Player player){
		player.getWorld().strikeLightning(player.getLocation());
		player.setFireTicks(200);
	}
	
	public void suffocate(Player player){
		double eh = player.getEyeHeight();
		Location l2 = player.getEyeLocation();
		while(!l2.getBlock().isEmpty() && l2.getY() > -4){ // Note: May drop you into the void
			l2 = l2.subtract(0, 1, 0);
		}
		l2 = l2.subtract(0, eh, 0);
		player.teleport(l2);
	}
	
	public void voidDrop(Player player){
		Location l = player.getLocation();
		l.setY(-5);
		player.teleport(l);
	}
	public void skyDrop(Player player){
		Location l = player.getLocation();
		l.setY(256);
		player.teleport(l);
	}
	public void incinerate(Player player){
		player.setFireTicks(10000);
	}
	public void starve(Player player){
		player.setFoodLevel(0);
		player.setSaturation(0);
	}
	public void fine(Player player, double amount){
		if(economy == null){
			player.setHealth(0);
			return;
		}
		if(economy.getBalance(player.getName()) < amount){
			player.setHealth(0);
			return;
		}else{
			economy.withdrawPlayer(player.getName(), amount);
			String cur = amount == 1 ? economy.currencyNameSingular() : economy.currencyNamePlural();
			player.sendMessage("You were fined " + amount + " " + cur + " for swearing.");
		}
	}
}
