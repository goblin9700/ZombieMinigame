package com.wlan222.ZombieMinigame.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.Lobby;
import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class JoinCommand implements CommandExecutor {
	private LobbyManager lm;

	public JoinCommand(LobbyManager lm) {
		this.lm = lm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("No Console Access!");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(createHelpText());
			return true;
		}
		if (args.length >= 1) {
			if (lm.lobbyIDs.containsKey(args[0])) {
				lm.lobbyIDs.get(args[0]).join((Player) sender);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED
						+ "The given Lobby doesn't exist!");
				return true;
			}
		}
		return true;
	}

	public String createHelpText() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n" + ChatColor.AQUA + "|     Name     |    ID    |\n");
		for (Lobby l : lm.lobbyList) {
			sb.append(ChatColor.GOLD + l.getName() + ChatColor.AQUA + " | "
					+ ChatColor.GOLD + l.id + "\n");
		}
		sb.append(ChatColor.AQUA + "-----------------------------\n");
		sb.append(ChatColor.AQUA + "/zs <Lobby ID>");
		return sb.toString();
	}
}
