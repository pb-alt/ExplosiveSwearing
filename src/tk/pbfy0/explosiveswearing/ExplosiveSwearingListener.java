package tk.pbfy0.explosiveswearing;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;



public class ExplosiveSwearingListener implements Listener {
	public static ExplosiveSwearing plugin;
	public static ExplosiveSwearingWorldGuard wg;
	public static Punishments punishments;
	
	public ExplosiveSwearingListener(ExplosiveSwearing instance, Punishments pi){
		plugin = instance;
		punishments = pi;
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		if(player.hasPermission("explosiveswearing.exempt"))
			return;
		
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
				if(wcaps >= 2){
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
					timesSworn++;
					ma2[j] = getCensored(tmd) + t2;
				}else{
					ma2[j] = i;
				}
				j++;
			}
		}
		if((plugin.swearing && timesSworn > 0) || (plugin.caps && timesCaps > 1)){
			punishments.randomlyPunish(player, timesSworn + timesCaps);
		}
		if(plugin.censor && plugin.swearing){
			message = joinArray(" ", ma2);
			event.setMessage(message);
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
}