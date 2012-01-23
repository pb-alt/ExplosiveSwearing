package org.dyndns.fishery.ExplosiveSwearing;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExplosiveSwearingWorldGuard {
	
	public static ExplosiveSwearing plugin;

	public ExplosiveSwearingWorldGuard(ExplosiveSwearing instance){
		plugin = instance;
	}
	
	
	private WorldGuardPlugin getWorldGuard() {
		Plugin wgplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if(wgplugin == null) {
			return null;
		}
		return (WorldGuardPlugin) wgplugin;
	}
	
	public boolean canBuildWG(Location p1, Location p2, Player player){
		WorldGuardPlugin wgp = getWorldGuard();
		if(wgp != null){
			int x1 = (int)p1.getX();
			int x2 = (int)p2.getX();
			int y1 = (int)p1.getY();
			int y2 = (int)p2.getY();
			int z1 = (int)p1.getZ();
			int z2 = (int)p2.getZ();
			for(;x1 < x2; x1++){
				for(;y1 < y2; y1++){
					for(;z1 < z2; z1++){
						Location cloc = new Location(player.getWorld(), x1, y1, z1);
						if(!(wgp.canBuild(player, cloc.getBlock()))){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
}
