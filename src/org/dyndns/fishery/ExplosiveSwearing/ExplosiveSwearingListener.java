package org.dyndns.fishery.ExplosiveSwearing;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ExplosiveSwearingListener implements Listener {
	public static ExplosiveSwearing plugin;
	private String curser;
	
	public ExplosiveSwearingListener(ExplosiveSwearing instance){
		plugin = instance;
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		List<Object> curses = plugin.getConfig().getList("curses");
		for(Object ctocheck : curses){
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
				GameMode gm = player.getGameMode();
				if(gm == GameMode.CREATIVE){
					player.setGameMode(GameMode.SURVIVAL);
				}
//				player.sendMessage("BOOM!");
				player.damage(100);
				if(gm != player.getGameMode()){
					player.setGameMode(gm);
				}
				return;
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		Player player;
		 if (!(event.getEntity() instanceof Player)){ //This should never happen now with PlayerDeathEvent, but you never know...
			 return;
		 }else{
			 player = (Player)event.getEntity();
		 }
		 if(this.curser.equals(player.getName())){
			 ((PlayerDeathEvent) event).setDeathMessage(null);
			 plugin.getServer().broadcastMessage(player.getName() + " swore explosively");
			 this.curser = "";
		 }
	}
}