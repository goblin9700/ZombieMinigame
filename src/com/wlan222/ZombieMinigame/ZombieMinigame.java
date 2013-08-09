package com.wlan222.ZombieMinigame;

import java.io.IOException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import com.wlan222.ZombieMinigame.commands.*;
import com.wlan222.ZombieMinigame.disguise.DisguiseCraft;
import com.wlan222.ZombieMinigame.disguise.LibsDisguises;
import com.wlan222.ZombieMinigame.disguise.iDisguise;
import com.wlan222.ZombieMinigame.listeners.SignListener;
import com.wlan222.ZombieMinigame.listeners.ZombieGamePlayerListener;
import com.wlan222.ZombieMinigame.manager.LobbyManager;
import com.wlan222.ZombieMinigame.manager.PlayerPerksManager;

public class ZombieMinigame extends JavaPlugin {

	// Build Settings
	// Enable MCStats.org Metrics
	public boolean useMetrics = true;
	// Enable Vault for Economy
	public boolean useVault = true;

	// Engine Variables. Do not change !
	public boolean econSetUp = false;
	public Economy econ;
	public LobbyManager lm;
	private String dm_name;
	private boolean useUpdater;

	public void onEnable() {
		if (!getConfig().isBoolean("metrics.enabled")) {
			getConfig().set("metrics.enabled", true);

		}
		if (!getConfig().isBoolean("updater.enabled")) {
			getConfig().set("updater.enabled", true);

		}
		useMetrics = getConfig().getBoolean("metrics.enabled");
		useUpdater = getConfig().getBoolean("updater.enabled");
		saveConfig();
		// Vault
		if (useVault) {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
				econ = null;

			} else {
				RegisteredServiceProvider<Economy> rsp = getServer()
						.getServicesManager().getRegistration(Economy.class);
				if (rsp == null) {
					econ = null;

				} else {
					econ = rsp.getProvider();
					econSetUp = true;
				}
			}
		}

		// Setup the game parts
		DisguiseManager dm = selectDisguiseManager();
		if (dm == null) {
			getLogger()
					.severe("No suitable Disguise Plugin found! Please install LibsDisguises!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		Game game = new Game(this, dm);
		lm = new LobbyManager(this, getConfig()
				.getInt("Settings.neededPlayers"), game);
		// Load extra libraries

		// Setup the lobby system

		game.initLobbys();
		PlayerPerksManager ppm = new PlayerPerksManager(this);
		getServer().getPluginManager().registerEvents(
				new ZombieGamePlayerListener(lm, ppm), this);
		getServer().getPluginManager().registerEvents(new SignListener(lm),
				this);
		getCommand("zs").setExecutor(new JoinCommand(lm));
		getCommand("zleave").setExecutor(new LeaveCommand(lm));
		getCommand("zr").setExecutor(new RadarCommand(lm, ppm));
		getCommand("zadmin").setExecutor(new AdminCommand(lm, game));
		getCommand("zperks").setExecutor(new PerksCommand(ppm, game, lm));

		for (Lobby l : lm.lobbyList) {
			l.updateSign();
		}
		// Metrics
		if (useMetrics) {
			try {
				Metrics metrics = new Metrics(this);
				Graph disguisePluginUsed = metrics
						.createGraph("Disguise Plugin");
				if (dm_name.equalsIgnoreCase("DisguiseCraft")) {
					disguisePluginUsed.addPlotter(new Metrics.Plotter(
							"DisguiseCraft") {

						@Override
						public int getValue() {
							return 1;
						}

					});
				}
				if (dm_name.equalsIgnoreCase("LibsDisguise")) {
					disguisePluginUsed.addPlotter(new Metrics.Plotter(
							"LibsDisguises") {

						@Override
						public int getValue() {
							return 1;
						}

					});
				}
				if (dm_name.equalsIgnoreCase("iDisguise")) {
					disguisePluginUsed.addPlotter(new Metrics.Plotter(
							"iDisguise") {

						@Override
						public int getValue() {
							return 1;
						}

					});
				}
				metrics.start();
			} catch (IOException e) {

			}
			if (useUpdater) {
				try {
					new AutoUpdate(this);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

	}

	public void onDisable() {
		for (Lobby l : lm.lobbyList) {
			l.broadcast("Game Aborted because Plugin is shutting down!");
			l.cleanup();
		}
	}

	public DisguiseManager selectDisguiseManager() {
		PluginManager pm = Bukkit.getPluginManager();
		DisguiseManager dm = null;
		String[] supportedPlugins = { "LibsDisguises", "iDisguise",
				"DisguiseCraft" };
		for (String s : supportedPlugins) {
			if (pm.getPlugin(s) != null) {
				dm = getDisguiseManagerFromName(s);
				dm_name = s;
				break;
			}
		}
		return dm;
	}

	private DisguiseManager getDisguiseManagerFromName(String s) {
		DisguiseManager dm = null;
		if (s.equals("LibsDisguises")) {
			getLogger().info("Using LibsDisguises");
			dm = new LibsDisguises();
		}
		if (s.equals("iDisguise")) {
			getLogger().info("Using iDisguise");
			getLogger()
					.warning(
							"iDisguise is not recommended. Please use LibsDisguises if possible!");
			dm = new iDisguise();
		}
		if (s.equals("DisguiseCraft")) {
			getLogger().info("Using DisguiseCraft");
			getLogger()
					.warning(
							"DisguiseCraft is not recommended. Please use LibsDisguises if possible!");
			dm = new DisguiseCraft();
		}
		return dm;
	}
}
