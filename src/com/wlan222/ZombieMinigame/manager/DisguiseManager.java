package com.wlan222.ZombieMinigame.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

public class DisguiseManager {
	private DisguiseCraftAPI dcAPI;
	private boolean isDC = false;
	private Plugin pl;

	public DisguiseManager(Plugin pl) {
		this.pl = pl;
	}

	public boolean useLegacy() {
		PluginManager pluginm = pl.getServer().getPluginManager();
		if (pluginm.getPlugin("LibsDisguises") == null) {
			return true;
		}
		return false;
	}

	public boolean serverHasDisguisePlugin() {
		PluginManager pluginm = pl.getServer().getPluginManager();
		if (pluginm.getPlugin("LibsDisguises") == null
				&& pluginm.getPlugin("DisguiseCraft") == null) {
			return false;
		}
		return true;
	}

	public boolean initDisguise() {
		if (!serverHasDisguisePlugin()) {
			pl.getLogger()
					.severe(ChatColor.RED
							+ "No Disguise Plugin found! Please install LibsDisguises.");
			return false;
		}
		if (useLegacy()) {
			pl.getLogger()
					.warning(
							ChatColor.RED
									+ "You are using DisguiseCraft as Disguise Plugin. I strongly recommend using LibsDisguises because it is way more stable!");
			pl.getLogger()
					.warning(
							ChatColor.RED
									+ "Bug Reports containing DisguiseCraft will be ignored!");
			return setupDisguiseCraft();
		} else {
			pl.getLogger().info("Using LibsDisguises");
			return setupLibsDisguises();
		}
	}

	private boolean setupLibsDisguises() {
		// We don't really have anything to be done here
		PluginManager pluginm = pl.getServer().getPluginManager();
		if (pluginm.getPlugin("LibsDisguises") == null) {
			return false;
		}
		return true;
	}

	private boolean setupDisguiseCraft() {
		PluginManager pluginm = pl.getServer().getPluginManager();
		Plugin p = pluginm.getPlugin("DisguiseCraft");
		if (!p.isEnabled()) {
			pl.getLogger().info("Using DisguiseCraft");
			pluginm.enablePlugin(p);

		}
		if (p.getConfig().isBoolean("disguisePVP")) {
			if (!p.getConfig().getBoolean("disguisePVP")) {
				pl.getLogger()
						.severe("DisguiseCraft disguisePVP is disabled! This Plugin will not work!");
				return false;
			}
		}
		boolean flag = true;
		try {
			dcAPI = DisguiseCraft.getAPI();
		} catch (Exception e) {
			pl.getLogger()
					.severe("An unexpected Error occured while starting DisguiseCraft API!");
			flag = false;
		}
		if (flag) {
			isDC = true;
		}
		return flag;
	}

	public boolean isDisguised(Player p) {
		if (isDC) {
			return dcAPI.isDisguised(p);
		} else {
			return me.libraryaddict.disguise.DisguiseAPI.isDisguised(p);
		}
	}

	public void disguise(Player p) {
		if (isDC) {
			dcAPI.disguisePlayer(
					p,
					new pgDev.bukkit.DisguiseCraft.disguise.Disguise(
							dcAPI.newEntityID(),
							pgDev.bukkit.DisguiseCraft.disguise.DisguiseType.Zombie));
		} else {
			me.libraryaddict.disguise.DisguiseAPI
					.disguiseToAll(
							p,
							new me.libraryaddict.disguise.DisguiseTypes.MobDisguise(
									me.libraryaddict.disguise.DisguiseTypes.DisguiseType.ZOMBIE,
									false, true));
		}
	}

	public void undisguise(Player p) {
		if (isDC) {
			dcAPI.undisguisePlayer(p);
		} else {
			me.libraryaddict.disguise.DisguiseAPI.undisguiseToAll(p);
		}
	}

}
