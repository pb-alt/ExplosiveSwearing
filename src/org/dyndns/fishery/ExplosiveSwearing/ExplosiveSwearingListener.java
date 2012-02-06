package org.dyndns.fishery.ExplosiveSwearing;

//import org.bukkit.GameMode;
import java.util.List;

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
	private String curser = "";
	
	public ExplosiveSwearingListener(ExplosiveSwearing instance, ExplosiveSwearingWorldGuard wgi){
		plugin = instance;
		wg = wgi;
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerChat(PlayerChatEvent event){
		if(event.isCancelled()){
			return;
		}
		Player player = event.getPlayer();
		String message = event.getMessage();
		String[] messaga = message.split(" ");
		String[] ma2 = new String[messaga.length];
		boolean hasSworn = false;
		List<Object> swears = plugin.getConfig().getList("curses");
		int j = 0;
		for(String i : messaga){
			String lastchar = i.substring(i.length()-1, i.length());
			String tmd = new String(i);

			if(lastchar.equals(".") || lastchar.equals("!") || lastchar.equals("?") || lastchar.equals(",")){
				tmd = i.substring(0, i.length() - 1);
			}
			if(swears.contains((Object)(tmd.toLowerCase()))){
				if(hasSworn == false && !(plugin.hasPerm(player, "explosiveswearing.exempt"))){
					goBoom(player);
					hasSworn = true;
				}
				ma2[j] = getCensored(i);
			}else{
				ma2[j] = i;
			}
			j++;
		}
		
		if(plugin.censor){
			message = joinArray(" ", ma2);
			event.setMessage(message);
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
			 this.curser = "";
		 }
	}
	private void goBoom(Player player){
		int explosionPower = 0;
		if(plugin.getConfig().getBoolean("explode")){
			Location loc = player.getLocation();
			Location p1 = loc.clone().subtract(3, 3, 3);
			Location p2 = loc.clone().add(3, 3, 3);
			explosionPower = wg.canBuildWG(p1, p2, player) ? 4 : 0;
		}
		this.curser = player.getName();
		player.sendMessage("BOOM!");
		player.getWorld().createExplosion(player.getLocation(), explosionPower);
		player.setHealth(0);
	}
    private String getCensored(String inp){
        String torepeat = "#!$%";
        String out = "";
        while(out.length() < inp.length()){
                out += torepeat;
        }
        return out.substring(0, inp.length());
    }

    private String joinArray(String glue, String[] array){
    	String o = array[0];
        int i;
        for(i = 1; i < array.length; i++){
        	o += glue + array[i];
        }
        return o;
    }
}