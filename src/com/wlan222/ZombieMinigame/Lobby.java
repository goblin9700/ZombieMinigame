package com.wlan222.ZombieMinigame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class Lobby {

	private ZombieMinigame pl;
	public String id;
	private FileConfiguration cfg;
	private File cfgf;
	private File lobbyFolder;
	private int lobbySize;
	private String region;
	private boolean gameRunning = false;
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> zombies = new ArrayList<Player>();
	private Player zombie;
	private Game g;
	private LobbyManager lm;
	private DisguiseManager dm;

	public Lobby(ZombieMinigame pl, String id, int lobbySize, Game g,
			LobbyManager lm, DisguiseManager dm) {
		this.pl = pl;
		this.id = id;
		this.lobbySize = lobbySize;
		checkConfig();
		setDefaultValues();
		lm.regions.put(region, this);
		this.g = g;
		this.lm = lm;
		this.dm = dm;
	}

	private void setDefaultValues() {
		FileConfiguration conf = getConfig();

		if (!conf.isString("lobby.name")) {
			conf.set("lobby.name", id);
		}
		if (!conf.isString("lobby.region")) {
			conf.set("lobby.region",
					"placeholder #Change this to a valid WorldGuard Region");
			this.setRegion(null);
		} else if (conf.getString("lobby.region").equals(
				"placeholder #Change this to a valid WorldGuard Region")) {
			this.setRegion(null);
		} else {
			this.setRegion(conf.getString("lobby.region"));
		}
		saveConfig();

	}

	private void checkConfig() {
		if (!pl.getDataFolder().exists()) {
			pl.getDataFolder().mkdir();
		}
		File lobbyFolder = new File(pl.getDataFolder(), "lobbys");
		if (!lobbyFolder.exists()) {
			lobbyFolder.mkdir();
		}
		this.lobbyFolder = lobbyFolder;
	}

	public void reloadConfig() {
		if (cfgf == null) {
			cfgf = new File(lobbyFolder, id + ".yml");
		}
		cfg = YamlConfiguration.loadConfiguration(cfgf);

	}

	public FileConfiguration getConfig() {
		if (cfg == null) {
			this.reloadConfig();
		}
		return cfg;
	}

	public void saveConfig() {
		if (cfg == null || cfgf == null) {
			return;
		}
		try {
			getConfig().save(cfgf);
		} catch (IOException ex) {
			pl.getLogger().log(Level.SEVERE,
					"Could not save config to " + cfgf, ex);
		}
	}

	public void broadcast(String message) {
		for (Player p : players) {
			p.sendMessage(message);
		}
	}

	public void broadcastExclusive(String message, ArrayList<Player> exclude) {
		for (Player p : players) {
			if (!exclude.contains(p)) {
				p.sendMessage(message);
			}
		}
	}

	public void broadcastExclusive(String message, Player exclude) {
		for (Player p : players) {
			if (!exclude.equals(p)) {
				p.sendMessage(message);
			}
		}
	}

	public Player getRandomPlayer() {
		Random r = new Random();
		return players.get(r.nextInt(players.size()));
	}

	public boolean onPreJoin(Player p) {
		if (getConfig().getString("lobby.region").equals(
				"placeholder #Change this to a valid WorldGuard Region")) {
			p.sendMessage("This Lobby isn't yet setup. Please notify an Admin!");
			return false;
		}
		if (!getConfig().isVector("lobby.spawn.human")
				|| !getConfig().isVector("lobby.spawn.zombie")
				|| !getConfig().isString("lobby.world")) {
			p.sendMessage("This Lobby isn't yet setup. Please notify an Admin!");
			return false;
		}
		if (pl.getServer().getWorld(getConfig().getString("lobby.world")) == null) {
			p.sendMessage("This Lobby isn't yet setup. Please notify an Admin!");
			return false;
		}
		if (pl.getConfig().isList("game.banned")) {
			if (pl.getConfig().getList("game.banned").contains(p.getName())) {
				p.sendMessage(ChatColor.RED
						+ "You've been banned from ZombieMinigame");
				return false;
			}
		}
		if (!p.hasPermission("ZombieGame.play")) {
			p.sendMessage(ChatColor.RED + "You need the Permission "
					+ ChatColor.GREEN + "ZombieGame.play" + ChatColor.RED
					+ " to play ZombieMinigame");
			return false;
		}

		if (lm.players.containsKey(p)) {
			p.sendMessage(ChatColor.RED + "You already joined another lobby!");
			return false;
		}

		if (players.size() >= lobbySize) {
			p.sendMessage(ChatColor.RED + "The Lobby " + ChatColor.GREEN + id
					+ ChatColor.RED + " is full. You cannot join!");
			return false;
		}
		if (gameRunning) {
			p.sendMessage(ChatColor.RED
					+ "The Game is currently running. You cannot join!");
			return false;
		}
		return true;
	}

	public void join(Player p) {
		if (!onPreJoin(p)) {
			return;
		}
		players.add(p);
		lm.players.put(p, this);
		p.sendMessage(ChatColor.GREEN + "You successfully joined the Lobby "
				+ ChatColor.RED + id);
		updateSign();
		if (players.size() >= lobbySize) {
			gameRunning = true;
			startGame();

		} else {
			broadcast(ChatColor.RED + String.valueOf(neededPlayer())
					+ ChatColor.GREEN
					+ " more Players are needed to start the Game!");
			updateSign();
		}
	}

	public void startGame() {
		Vector humanSpawn = getConfig().getVector("lobby.spawn.human");
		Vector zombieSpawn = getConfig().getVector("lobby.spawn.zombie");
		World w = pl.getServer().getWorld(getConfig().getString("lobby.world"));
		zombie = getRandomPlayer();
		for (Player p : players) {
			if (p.equals(zombie)) {
				p.teleport(zombieSpawn.toLocation(w));
			} else {
				p.teleport(humanSpawn.toLocation(w));
			}
		}
		broadcastExclusive(ChatColor.GOLD + zombie.getName() + ChatColor.RED
				+ " is Patient Zero!", zombie);
		zombie.sendMessage(ChatColor.RED
				+ "You are the first Zombie! Hit the others to turn them into Zombies!");
		dm.disguise(zombie);
		zombies.add(zombie);
		updateSign();
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public boolean isZombie(Player p) {
		return zombies.contains(p);
	}

	public Location getHumanSpawn() {
		Vector v = getConfig().getVector("lobby.spawn.human");
		World w = pl.getServer().getWorld(getConfig().getString("lobby.world"));
		return v.toLocation(w);
	}

	public Location getZombieSpawn() {
		Vector v = getConfig().getVector("lobby.spawn.zombie");
		World w = pl.getServer().getWorld(getConfig().getString("lobby.world"));
		return v.toLocation(w);
	}

	public void setSpawnPoint(boolean human, Location loc) {
		if (human) {
			getConfig().set("lobby.spawn.human", loc.toVector());
			getConfig().set("lobby.world", loc.getWorld().getName());
		} else {
			getConfig().set("lobby.spawn.zombie", loc.toVector());
			getConfig().set("lobby.world", loc.getWorld().getName());
		}
		saveConfig();
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void leave(Player player) {
		if (!gameRunning) {
			players.remove(player);
			lm.players.remove(player);

			broadcast(ChatColor.RED + player.getName() + " left the Lobby!\n"
					+ ChatColor.RED + String.valueOf(neededPlayer())
					+ ChatColor.GREEN
					+ " more Players are needed to start the Game!");
			updateSign();
		}
		if (isZombie(player)) {
			if (player.equals(zombie)) {
				broadcast(ChatColor.RED + zombie.getName() + ChatColor.GOLD
						+ " left the game. Reshuffling Patient Zero!");
				players.remove(player);
				lm.players.remove(player);
				zombie = getRandomPlayer();
				broadcastExclusive(ChatColor.RED + zombie.getName()
						+ ChatColor.GOLD + " is the new Patient Zero!", zombie);
				zombie.sendMessage(ChatColor.GREEN
						+ "You are the new Patient Zero!");

				if (checkForWin()) {
					Player winner = getWinner();
					broadcastExclusive(ChatColor.RED + winner.getName()
							+ ChatColor.GREEN + " won the Game!", winner);
					winner.sendMessage(ChatColor.GREEN + "You won the Game!");
					g.rewardPlayer(winner, 10);
					cleanup();
				}
			}
			updateSign();
		}
	}

	public void zombify(Player p, Player hurter) {
		zombies.add(p);

		p.sendMessage(ChatColor.RED + hurter.getName() + ChatColor.GREEN
				+ " turned you into a Zombie! Hit others to make them Zombies!");
		dm.disguise(p);
		if (checkForWin()) {

			broadcastExclusive(ChatColor.RED + p.getName() + ChatColor.GREEN
					+ " was the last Survivor!", p);
			p.sendMessage(ChatColor.GREEN + "You were the last Survivor!");
			g.rewardPlayer(p, 15);

			broadcastExclusive(ChatColor.RED + zombie.getName()
					+ ChatColor.GREEN + " won the Game!", zombie);
			zombie.sendMessage(ChatColor.GREEN + "You won the Game!");
			g.rewardPlayer(zombie, 10);

			cleanup();
		}
	}

	public boolean checkForWin() {
		if (players.size() != 1) {
			if (players.size() > zombies.size()) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public Player getWinner() {
		if (players.size() != 1) {
			if (players.size() > zombies.size()) {
				return null;
			} else {
				return zombie;
			}
		} else {
			return players.get(0);
		}
	}

	public void cleanup() {
		gameRunning = false;
		for (Player p : players) {

			if (dm.isDisguised(p)) {
				dm.undisguise(p);
			}

			p.teleport(p.getWorld().getSpawnLocation());
			lm.players.remove(p);
		}
		players.clear();
		zombies.clear();
		updateSign();
	}

	public void setSign(Sign s) {
		getConfig().set("lobby.sign.location", s.getLocation().toVector());
		getConfig().set("lobby.sign.world",
				s.getLocation().getWorld().getName());
		saveConfig();
	}

	public Sign getSign() {
		Sign s = (Sign) getConfig()
				.getVector("lobby.sign.location")
				.toLocation(
						pl.getServer().getWorld(
								getConfig().getString("lobby.sign.world")))
				.getBlock().getState();
		return s;
	}

	public boolean isSignSetup() {
		if (!getConfig().isVector("lobby.sign.location")) {
			return false;
		}
		if (!getConfig().isString("lobby.sign.world")) {
			return false;
		}
		if (pl.getServer().getWorld(getConfig().getString("lobby.sign.world")) == null) {
			return false;
		}
		Material m = getConfig()
				.getVector("lobby.sign.location")
				.toLocation(
						pl.getServer().getWorld(
								getConfig().getString("lobby.sign.world")))
				.getBlock().getType();
		if (m != Material.WALL_SIGN && m != Material.SIGN_POST) {
			return false;
		}
		return true;
	}

	public void updateSign() {
		reloadConfig();
		if (!isSignSetup()) {
			return;
		}
		Sign s = getSign();
		s.setLine(0, ChatColor.BLUE + "[" + id + "]");
		if (isGameRunning()) {
			s.setLine(1, ChatColor.RED + "Started");
			s.setLine(2, ChatColor.BLUE + String.valueOf(players.size()) + "/"
					+ lobbySize);
		} else {
			s.setLine(1, ChatColor.GREEN + "Open");
			s.setLine(2, ChatColor.BLUE + String.valueOf(players.size()) + "/"
					+ lobbySize);
		}
		s.update();

	}

	public int neededPlayer() {

		int neededPlayers = lobbySize - players.size();
		return neededPlayers;
	}

	public String getName() {
		return getConfig().getString("lobby.name");
	}
}
