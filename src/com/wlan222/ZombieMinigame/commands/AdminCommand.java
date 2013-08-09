package com.wlan222.ZombieMinigame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.Game;
import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class AdminCommand implements CommandExecutor {
	private LobbyManager lm;
	private Game g;

	public AdminCommand(LobbyManager lm, Game g) {
		this.lm = lm;
		this.g = g;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("No Console Access!");
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission("ZombieGame.admin")) {
			return false;
		}
		if (args.length <= 1) {
			p.sendMessage(getHelpText());
			return true;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("sethumanspawn")) {
				if (!lm.lobbyIDs.containsKey(args[1])) {
					p.sendMessage("Lobby ID not found!");
					return true;
				}
				lm.lobbyIDs.get(args[1]).setSpawnPoint(true, p.getLocation());
				p.sendMessage("Successfully set human spawn point");
				return true;
			} else if (args[0].equalsIgnoreCase("setzombiespawn")) {
				if (!lm.lobbyIDs.containsKey(args[1])) {
					p.sendMessage("Lobby ID not found!");
					return true;
				}
				lm.lobbyIDs.get(args[1]).setSpawnPoint(false, p.getLocation());
				p.sendMessage("Successfully set zombie spawn point");
				return true;
			} else {
				p.sendMessage(getHelpText());
				return true;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("givewinpoints")) {
				if (!isInteger(args[2])) {
					p.sendMessage("Amount has to be a Number!");
					return true;
				}
				g.pay(p, ( Integer.parseInt(args[2]) * -1));
				p.sendMessage("Gave " + args[1] + " " + args[2] + " WinPoints");
			} else {
				p.sendMessage(getHelpText());
				return true;
			}
		}
		return true;
	}

	public String getHelpText() {
		StringBuilder sb = new StringBuilder();
		String[] cmds = { "sethumanspawn <Lobby ID>",
				"setzombiespawn <Lobby ID>", "givewinpoints <Name> <Amount>" };
		sb.append("Admin Commands\n------------------\n");
		for (String s : cmds) {
			sb.append("/zadmin " + s + "\n");
		}
		return sb.toString();
	}

	public boolean isInteger(String s) {
		boolean flag = true;
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			flag = false;
		}
		return flag;
	}
}
