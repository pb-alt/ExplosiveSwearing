package org.dyndns.fishery.ExplosiveSwearing;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class ExplosiveSwearing extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final ExplosiveSwearingListener listener = new ExplosiveSwearingListener(this);

	public void onEnable(){
		log.info("[ExplosiveSwearing]: Enabled");
		File cfgfile = new File(this.getDataFolder(), "config.yml");
		if(!(this.getDataFolder().exists())){
			this.getDataFolder().mkdirs();
		}
		if(!(cfgfile.exists() || this.getDataFolder().canWrite())){
			try{
				cfgfile.createNewFile();
				this.getConfig().options().copyDefaults(true);
			} catch(IOException e){
				log.warning(e.toString());
			}			
		}
		
//		}
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(listener, this);
		pm.registerEvents(listener, this);
	}

	public void onDisable(){
		log.info("[ExplosiveSwearing]: Disabled");
	}

}
