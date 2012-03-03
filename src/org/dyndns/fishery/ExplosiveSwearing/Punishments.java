package org.dyndns.fishery.ExplosiveSwearing;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Punishments {
	public static ExplosiveSwearingWorldGuard wg;
	public Punishments(ExplosiveSwearingWorldGuard wgi){
		wg = wgi;
	}
	public Punishments(){
		wg = null;
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
		while(l2.getBlock().getTypeId() == 0 && l2.getY() > -4){ // Note: May drop you into the void
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
}
