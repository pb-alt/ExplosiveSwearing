package org.dyndns.fishery.ExplosiveSwearing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

import org.bukkit.ChatColor;
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
	public static Punishments punishments;
	private Map<String, String> curser = new HashMap<String, String>();
	private Random rng;
	
	public ExplosiveSwearingListener(ExplosiveSwearing instance, Punishments pi){
		plugin = instance;
		rng = new Random();
		punishments = pi;
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
		int timesSworn = 0;
		int timesCaps = 0;
		int j = 0;
		for(String i : messaga){
			i = ChatColor.stripColor(i);
			if(plugin.caps){
				int wcaps = 0;
				for(int k = 0; k < i.length(); k++){
					if(Character.isUpperCase(i.charAt(k))){
						wcaps++;
					}
				}
				if(wcaps >= 2 && !(plugin.hasPerm(player, "explosiveswearing.exempt"))){
					timesCaps++;
				}
			}
			if(plugin.swearing){
				String tmd = i;
				if(i.length() <= 0){
					continue;
				}
				char lastchar = i.charAt(i.length()-1);
				String t2 = "";
	
				while(lastchar == '.' || lastchar == '!' || lastchar == '?' || lastchar == ','){
					tmd = tmd.substring(0, tmd.length() - 1);
					t2 = Character.toString(lastchar) + t2;
					if(tmd.length() == 0){
						break;
					}
					lastchar = tmd.charAt(tmd.length()-1);
				}
				tmd = removePunctuation(tmd);
				if(plugin.curses.contains(tmd.toLowerCase())){
					if(!(plugin.hasPerm(player, "explosiveswearing.exempt"))){
						timesSworn++;
					}
					ma2[j] = getCensored(tmd) + t2;
				}else{
					ma2[j] = i;
				}
				j++;
			}
		}
		if((plugin.swearing && timesSworn > 0) || (plugin.caps && timesCaps > 1)){
			randomlyPunish(player, timesSworn + timesCaps);
		}
		if(plugin.censor && plugin.swearing){
			message = joinArray(" ", ma2);
			event.setMessage(message);
		}
	}
	
	private void setCurser(String player, String message){
		curser.put(player, message);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		Player player;
		 if (!(event.getEntity() instanceof Player)){
			 return;
		 }
		 player = (Player)event.getEntity();
		 String dm = curser.get(player.getName());
		 if(dm != null){
			 ((PlayerDeathEvent) event).setDeathMessage(player.getName() + " " + dm);
			 curser.remove(player.getName());
		 }
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
    
    private String removePunctuation(String inp){
    	inp = inp.replaceAll("[!#%^&*(),./\\\\?_\\-|\\[\\]{}]", "");
    	inp = inp.replaceAll("@", "a");
    	inp = inp.replaceAll("\\$", "s");
    	return inp;
    }
    private void randomlyPunish(Player player, int amount){
    	player.sendMessage("Ouch!");
    	if(!(plugin.broadcast == null) && !plugin.broadcast.equals("")){
    		plugin.getServer().broadcastMessage(plugin.broadcast.replaceAll("\\[name\\]", Matcher.quoteReplacement(player.getName())));
    	}
    	if(!(plugin.PM == null) && !plugin.PM.equals("")){
    		player.sendMessage(plugin.PM.replaceAll("\\[name\\]", Matcher.quoteReplacement(player.getName())));
    	}
    	double random = rng.nextDouble();
    	double c = 0;
    	if(random < (c += plugin.chances.explode)){
			if(plugin.explodable){
				setCurser(player.getName(), plugin.caps ? "used explosive caps" : "swore explosively");
			}
			punishments.goBoom(player, plugin.explodable);
    	}else if(random < (c += plugin.chances.lightning)){
    		if(plugin.kill){
    			setCurser(player.getName(), "was struck down");
    		}
    		punishments.lightning(player);
    		if(plugin.kill){
    			player.setHealth(0);
    		}
    	}else if(random < (c += plugin.chances.suffocate)){
    		punishments.suffocate(player);
    	}else if(random < (c += plugin.chances.voiddrop)){
    		if(plugin.kill){
    			setCurser(player.getName(), "was voided");
    		}
    		punishments.voidDrop(player);
    		if(plugin.kill){
    			player.setHealth(0);
    		}
    	}else if(random < (c += plugin.chances.sky)){
    		punishments.skyDrop(player);
    	}else if(random < (c += plugin.chances.incinerate)){
    		punishments.incinerate(player);
    	}else if(random < (c += plugin.chances.starve)){
    		punishments.starve(player);
    	}else if(random < (c += plugin.chances.fine)){
    		punishments.fine(player, amount * plugin.fine);
    	}
    }
}