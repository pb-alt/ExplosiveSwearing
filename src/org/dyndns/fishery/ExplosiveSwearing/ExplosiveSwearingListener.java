package org.dyndns.fishery.ExplosiveSwearing;

//import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class ExplosiveSwearingListener implements Listener {
	public static ExplosiveSwearing plugin;
	public static ExplosiveSwearingWorldGuard wg;
	private String curser;
	
	public ExplosiveSwearingListener(ExplosiveSwearing instance, ExplosiveSwearingWorldGuard wgi){
		plugin = instance;
		wg = wgi;
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
//		List<Object> curses = plugin.getConfig().getList("curses");
		for(Object ctocheck : plugin.getConfig().getList("curses")){
			String tmsg = new String(message);
			if(!(tmsg.substring(0, 1).equals(" "))){
				tmsg = " " + tmsg;
			}
			String tstr = tmsg.substring(tmsg.length() - 1, tmsg.length());
			
			if(!(tstr.equals(" ") || tstr.equals(".") || tstr.equals("!"))){
				tmsg += " ";
			}
			if(tmsg.toLowerCase().matches(".* " + ctocheck.toString().toLowerCase() + "[\\x21\\x2e ].*")){
				this.curser = player.getName();
//				GameMode gm = player.getGameMode();
//				if(gm == GameMode.CREATIVE){
//					player.setGameMode(GameMode.SURVIVAL);
//				}
				int explosionPower = 0;
				if(plugin.getConfig().getBoolean("explode")){
					Location loc = player.getLocation();
					Location p1 = loc.clone().subtract(3, 3, 3);
					Location p2 = loc.clone().add(3, 3, 3);
					explosionPower = wg.canBuildWG(p1, p2, player) ? 4 : 0;
				}
				player.sendMessage("BOOM!");
				player.getWorld().createExplosion(player.getLocation(), explosionPower);
				player.setHealth(0);
//				if(gm != player.getGameMode()){
//					player.setGameMode(gm);
//				}
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		Player player;
		 if (!(event.getEntity() instanceof Player)){
			 return;
		 }
		 player = (Player)event.getEntity();
		 if(this.curser.equals(player.getName())){
			 ((PlayerDeathEvent) event).setDeathMessage(player.getName() + " swore explosively");
//			 plugin.getServer().broadcastMessage(player.getName() + " swore explosively");
			 this.curser = "";
		 }
	}
}