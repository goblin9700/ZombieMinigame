package com.wlan222.ZombieMinigame.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class LeaveCommand implements CommandExecutor {

	private LobbyManager lm;

	public LeaveCommand(LobbyManager lm) {
		this.lm = lm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("No Console Access!");
			return true;
		}
		Player p = (Player) sender;
		if (!lm.players.containsKey(p)) {
			p.sendMessage(ChatColor.RED + "You aren't in a Lobby!");
			return true;
		}
		if (lm.players.get(p).isGameRunning()) {
			p.sendMessage(ChatColor.RED
					+ "You cannot leave the Game if the game already started!");
		}
		lm.players.get(p).leave(p);
		p.teleport(p.getWorld().getSpawnLocation());
		return true;
	}

}
