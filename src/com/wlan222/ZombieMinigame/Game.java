package com.wlan222.ZombieMinigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class Game {
	private ZombieMinigame pl;
	private LobbyManager lm;
	private DisguiseManager dm;

	public Game(ZombieMinigame pl, DisguiseManager dm) {
		this.pl = pl;
		this.dm = dm;

	}

	public void setLM(LobbyManager lm) {
		this.lm = lm;
	}



	public void initLobbys() {
		if (!pl.getConfig().isInt("game.lobbys.amount")) {
			pl.getConfig().set("game.lobbys.amount", 5);
		}
		if (!pl.getConfig().isInt("Settings.neededPlayers")) {
			pl.getConfig().set("Settings.neededPlayers", 8);
		}

		for (int i = 0; i != pl.getConfig().getInt("game.lobbys.amount"); i++) {
			lm.addLobby(dm);
		}
		pl.saveConfig();
	}

	public boolean hasEnoughMoney(Player p, int amount) {
		if (loadWinPoints().get(p.getName()) >= amount) {
			return true;
		} else {
			return false;
		}
	}

	public int getMoney(Player p) {
		if (!loadWinPoints().containsKey(p.getName())) {
			return 0;
		} else {
			return loadWinPoints().get(p.getName());
		}

	}

	public void pay(Player p, int amount) {
		HashMap<String, Integer> winpoints = loadWinPoints();
		if (winpoints.containsKey(p.getName())) {
			winpoints.put(p.getName(), (winpoints.get(p.getName()) - amount));
		} else {
			winpoints.put(p.getName(), (0 - amount));
		}

		saveWinPoints(winpoints);
	}

	public void pay(String p, int amount) {
		HashMap<String, Integer> winpoints = loadWinPoints();
		winpoints.put(p, (winpoints.get(p) - amount));
		saveWinPoints(winpoints);
	}

	public void rewardPlayer(Player p, int winpoints) {
		if (!pl.getConfig().isBoolean("reward.money.enabled")) {
			pl.getConfig().set("reward.money.enabled", false);
		}
		if (!pl.getConfig().isInt("reward.money.amount")) {
			pl.getConfig().set("reward.money.amount", 5);
		}
		int money = pl.getConfig().getInt("reward.money.amount");
		boolean vaultEnabled = pl.getConfig()
				.getBoolean("reward.money.enabled");
		if (pl.useVault && vaultEnabled && pl.econSetUp) {
			if (!pl.econ.hasAccount(p.getName())) {
				pl.econ.createPlayerAccount(p.getName());
			}
			pl.econ.bankDeposit(p.getName(), money);
			p.sendMessage(ChatColor.RED + String.valueOf(money) + " "
					+ pl.econ.currencyNameSingular() + ChatColor.GOLD
					+ " were added to you bank account");
		}
		if (!new File(pl.getDataFolder(), "winpoints").exists()) {
			HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
			hashmap.put(p.getName(), winpoints);
			saveWinPoints(hashmap);
			p.sendMessage(ChatColor.RED
					+ String.valueOf(winpoints)
					+ ChatColor.GOLD
					+ " Win Points were added to your Perk Shop Account.\nYou now have "
					+ ChatColor.RED + winpoints + ChatColor.GOLD
					+ " Win Points\nVisit the Perk Shop using /zperks");
			return;
		} else {
			HashMap<String, Integer> hashmap = loadWinPoints();
			int amount = 0;
			if (hashmap.containsKey(p.getName())) {
				amount = hashmap.get(p.getName());
			}
			int newamount = winpoints + amount;
			hashmap.put(p.getName(), newamount);
			saveWinPoints(hashmap);
			p.sendMessage(ChatColor.RED
					+ String.valueOf(winpoints)
					+ ChatColor.GOLD
					+ " Win Points were added to your Perk Shop Account.\nYou now have "
					+ ChatColor.RED + newamount + ChatColor.GOLD
					+ " Win Points\nVisit the Perk Shop using /zperks");
			return;
		}

	}

	private void saveWinPoints(HashMap<String, Integer> hashmap) {
		String filename = "winpoints";
		try {
			FileOutputStream fos = new FileOutputStream(new File(
					pl.getDataFolder(), filename));
			GZIPOutputStream gzos = new GZIPOutputStream(fos);
			ObjectOutputStream out = new ObjectOutputStream(gzos);
			out.writeObject(hashmap);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> loadWinPoints() {
		String filename = "winpoints";
		HashMap<String, Integer> out = null;
		if (!new File(pl.getDataFolder(), filename).exists()) {
			HashMap<String, Integer> newHM = new HashMap<String, Integer>();
			saveWinPoints(newHM);
		}
		try {
			FileInputStream fin = new FileInputStream(new File(
					pl.getDataFolder(), filename));
			GZIPInputStream gzin = new GZIPInputStream(fin);
			ObjectInputStream in = new ObjectInputStream(gzin);
			Object o = in.readObject();
			if (!(o instanceof HashMap<?, ?>)) {

				pl.getLogger().severe(
						"Fatal Error! Winpoints file was corrupted!");
				out = new HashMap<String, Integer>();
			} else {
				out = (HashMap<String, Integer>) o;
			}
			in.close();
			gzin.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
}
