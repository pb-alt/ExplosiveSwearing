package org.dyndns.fishery.ExplosiveSwearing;
import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class ExplosiveSwearing extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final ExplosiveSwearingListener listener = new ExplosiveSwearingListener(this);

	public void onEnable(){
		log.info("[ExplosiveSwearing]: Enabled");
		
		if(!(new File(this.getDataFolder(), "config.yml").exists())){
			this.getConfig().options().copyDefaults(true);
		}
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(listener, this);
		pm.registerEvents(listener, this);
	}

	public void onDisable(){
		log.info("[ExplosiveSwearing]: Disabled");
	}

}
