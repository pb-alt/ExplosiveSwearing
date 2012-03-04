package org.dyndns.fishery.ExplosiveSwearing;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class ExplosiveSwearing extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private ExplosiveSwearingWorldGuard wg = new ExplosiveSwearingWorldGuard(this);
	public boolean explodable = true;
	public boolean censor = false;
	public boolean opPerms = false;
	public boolean kill = false;
	public Chances chances = new Chances();
	public Economy economy = null;
	private Punishments punishments;
	private ExplosiveSwearingListener listener;
	public double fine = 0;
	List<String> curses;
	boolean caps = false;
	boolean swearing = true;
	String PM = "";
	String broadcast = "";

	
	public void onEnable(){
		loadCfgVars();
		if(!(this.getDataFolder().exists())){
			this.getDataFolder().mkdirs();
		}
		if(!(new File(this.getDataFolder(), "config.yml").exists())){
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
		}
		if(!setupEconomy()){
			log.info("[ExplosiveSwearing] No economy found. Fining will not work.");
		}
		punishments = new Punishments(economy, wg);
		listener = new ExplosiveSwearingListener(this, punishments);
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
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	private void loadCfgVars(){
		FileConfiguration cfg = this.getConfig();
		explodable = cfg.getBoolean("punishments.explode");
		censor = cfg.getBoolean("censor");
		kill = cfg.getBoolean("punishments.kill");
		fine = cfg.getDouble("punishments.fine");
		swearing = cfg.getBoolean("watch.swearing");
		curses = cfg.getStringList("curses");
		caps = cfg.getBoolean("watch.caps");
		broadcast = cfg.getString("extra.broadcast");
		PM = cfg.getString("extra.PM");
		double explode = cfg.getDouble("chances.explode");
		double lightning = cfg.getDouble("chances.lightning");
		double suffocate = cfg.getDouble("chances.suffocate");
		double voiddrop = cfg.getDouble("chances.void");
		double sky = cfg.getDouble("chances.sky");
		double incinerate = cfg.getDouble("chances.incinerate");
		double starve = cfg.getDouble("chances.starve");
		double fine = cfg.getDouble("chances.fine");
		double total = explode + lightning + suffocate + voiddrop + sky+ incinerate + starve + fine;
		chances.explode = explode / total;
		chances.lightning = lightning / total;
		chances.suffocate = suffocate / total;
		chances.voiddrop = voiddrop / total;
		chances.sky = sky / total;
		chances.incinerate = incinerate / total;
		chances.starve = starve / total;
		chances.fine = fine / total;
	}
	public boolean hasPerm(CommandSender player, String perm){
		return (player instanceof ConsoleCommandSender) ||(opPerms && player.isOp()) || (player.isPermissionSet(perm) && player.hasPermission(perm));
	}
}

class Chances{
	public double explode = 0;
	public double lightning = 0;
	public double suffocate = 0;
	public double voiddrop = 0;
	public double sky = 0;
	public double incinerate = 0;
	public double starve = 0;
	public double fine = 0;
	public Chances(){
		
	}
}