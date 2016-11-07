package de.janisworld.restingblocks;

import java.io.File;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class RestingBlocks extends JavaPlugin implements Listener, CommandExecutor{

	public static ConsoleCommandSender console;

	@Override
	public void onDisable() {
		console.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&7[&c&l-&7] &8" + getDescription().getName() + " is disabled."));
	}

	@Override
	public void onEnable() {
		console = getServer().getConsoleSender();
		console.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&7[&2&l+&7] &8" + getDescription().getName() + " is enabled."));

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		getCommand("addRest").setExecutor(this);
		getCommand("delRest").setExecutor(this);
		
		// Config
		if (!new File(getDataFolder(), "config.yml").exists()) {
			getDataFolder().mkdir();
			getConfig().addDefault("RestingBlocks.enabled", true);
			getConfig().addDefault("RestingBlocks.prefix", "&7[&6RestingBlocks&7]");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String lbl, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" You have to be a player to execute this command."));
			return true;
		}
		Player p = (Player) cs;
		if(p.hasPermission("restingblocks")){
			if(args.length != 0){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" Use /addRest or /delRest."));
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("addRest")){
				int x = p.getLocation().getBlockX();
				int y = p.getLocation().getBlockY();
				int z = p.getLocation().getBlockZ();
				List<String> a = getConfig().getStringList("RestingBlocks.rests");
				if(a.contains(x+"#"+y+"#"+z)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" Here is already a RestSpot."));
					return true;
				}
				a.add(x+"#"+y+"#"+z);
				getConfig().set("RestingBlocks.rests", a);
				saveConfig();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" You have successfully added this spot."));
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("delRest")){
				int x = p.getLocation().getBlockX();
				int y = p.getLocation().getBlockY();
				int z = p.getLocation().getBlockZ();
				List<String> a = getConfig().getStringList("RestingBlocks.rests");
				if(!a.contains(x+"#"+y+"#"+z)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" This spot isn't a RestSpot."));
					return true;
				}
				a.remove(x+"#"+y+"#"+z);
				getConfig().set("RestingBlocks.rests", a);
				saveConfig();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" You have successfully deleted this spot."));
				return true;
			}
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RestingBlocks.prefix")+" You haven't the right permission."));
		return true;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		for(String a : getConfig().getStringList("RestingBlocks.rests")){
			int x = p.getLocation().getBlockX();
			int y = p.getLocation().getBlockY();
			int z = p.getLocation().getBlockZ();
			if(a.equals(x+"#"+y+"#"+z)){
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20, 2, true, true, Color.LIME));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 2, true, true, Color.LIME));
			}
		}
	}
	
}
