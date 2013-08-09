package com.wlan222.ZombieMinigame.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.Perks;
import com.wlan222.ZombieMinigame.manager.LobbyManager;
import com.wlan222.ZombieMinigame.manager.PlayerPerksManager;

public class RadarCommand implements CommandExecutor {
	private LobbyManager lm;
	private PlayerPerksManager ppm;

	public RadarCommand(LobbyManager lm, PlayerPerksManager ppm) {
		this.lm = lm;
		this.ppm = ppm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("No Console Access!");
			return true;
		}
		Player p = (Player) sender;
		if (!lm.players.containsKey(p)) {
			p.sendMessage(ChatColor.RED
					+ "This command can only be used if you are in a ZombieMinigame");
			return true;

		}
		if (!lm.players.get(p).isGameRunning()) {
			p.sendMessage(ChatColor.RED
					+ "This Perk can only be activate when the game started!");
			return true;
		}
		if (!ppm.hasPerk(p)) {
			p.sendMessage(ChatColor.RED
					+ "You can only access this command once if you bought the perk");
			return true;
		}
		if (!ppm.getPerk(p).equals(Perks.ZOMBIERADAR)) {
			p.sendMessage(ChatColor.RED
					+ "You can only access this command once if you bought the perk");
			return true;
		}
		for (Player plr : lm.players.get(p).zombies) {
			p.sendMessage(ChatColor.AQUA + plr.getName() + ":\nX:"
					+ plr.getLocation().getBlockX() + "\nY:"
					+ plr.getLocation().getBlockY() + "\nZ:"
					+ plr.getLocation().getBlockY() + "\nDistance:"
					+ plr.getLocation().distance(p.getLocation()) + "\n");
		}
		ppm.removePerk(p);
		return true;
	}
}
