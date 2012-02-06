package org.dyndns.fishery.ExplosiveSwearing;
import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class ExplosiveSwearing extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private ExplosiveSwearingWorldGuard wg = new ExplosiveSwearingWorldGuard(this);
	private final ExplosiveSwearingListener listener = new ExplosiveSwearingListener(this, wg);
	public boolean explodable = true;
	public boolean censor = true;
	public boolean opPerms = false;

	
	public void onEnable(){
		loadCfgVars();
		if(!(this.getDataFolder().exists())){
			this.getDataFolder().mkdirs();
		}
		if(!(new File(this.getDataFolder(), "config.yml").exists())){
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
		}
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(listener, this);
		opPerms = (pm.getPlugin("PermissionsBukkit") == null && pm.getPlugin("bPermissions") == null && pm.getPlugin("PermissionsEx") == null);
		if(opPerms){
			log.info("[ExplosiveSwearing] No permissions detected. Defaulting to op");
		}
//		pm.registerEvents(listener, this);
		log.info("[ExplosiveSwearing] Enabled");
	}

	public void onDisable(){
		log.info("[ExplosiveSwearing] Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length == 0){
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("exswear")){
			String plCommand = args[0];
			if(plCommand.equals("reload") && args.length == 1){
				Player sendp = null;
				if(sender instanceof Player){
					sendp = (Player) sender;
				}
				if(!(sender instanceof ConsoleCommandSender || hasPerm(sendp, "explosiveswearing.reload"))){
					sendp.sendMessage(ChatColor.RED + "You do not have permission to reload this plugin");
					return true;
				}
				log.info("[ExplosiveSwearing] Loading config");
				if(sendp != null){
					sendp.sendMessage("[ExplosiveSwearing] Loading config");
				}
				this.reloadConfig();
				loadCfgVars();
				log.info("[ExplosiveSwearing] Loaded config");
				if(sendp != null){
					sendp.sendMessage("[ExplosiveSwearing] Loaded config");
				}
				return true;
			}/*else if(plCommand.equals("addword") && args.length == 2){
				swearwords.ensureCapacity(swearwords.size() + 1);
				swearwords.add(args[1]);
				return true;
			}*/
		}
		return false;
	}
	
	private void loadCfgVars(){
		explodable = this.getConfig().getBoolean("explode");
		censor = this.getConfig().getBoolean("censor");
	}
	public boolean hasPerm(Player player, String perm){
		return (opPerms && player.isOp()) || (player.isPermissionSet(perm) && player.hasPermission(perm));
	}
}
