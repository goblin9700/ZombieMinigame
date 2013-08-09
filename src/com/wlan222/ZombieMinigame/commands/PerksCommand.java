package com.wlan222.ZombieMinigame.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.Game;
import com.wlan222.ZombieMinigame.Perks;
import com.wlan222.ZombieMinigame.manager.LobbyManager;
import com.wlan222.ZombieMinigame.manager.PlayerPerksManager;

public class PerksCommand implements CommandExecutor {
	private PlayerPerksManager ppm;
	private Game g;
	private LobbyManager lm;

	public PerksCommand(PlayerPerksManager ppm, Game g, LobbyManager lm) {
		this.ppm = ppm;
		this.g = g;
		this.lm = lm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("No Console Access!");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(createHelpText((Player) sender));
			return true;
		}
		Player p = (Player) sender;
		if (lm.players.containsKey(p)) {
			sender.sendMessage(ChatColor.RED
					+ "You cannot buy perks while you are in a Lobby!");
			return true;
		}
		if (args.length >= 1) {
			if (ppm.hasPerk(p)) {
				p.sendMessage(ChatColor.GOLD + "You already have the perk "
						+ ppm.getPerk(p));
				return true;
			}
			if (args[0].equalsIgnoreCase("invisible")) {
				if (!g.hasEnoughMoney(p, 15)) {
					p.sendMessage(ChatColor.RED
							+ "You don't have enough Money!");
					return true;
				}
				g.pay(p, 15);
				ppm.addPerk(p, Perks.INVISIBLE);
			} else if (args[0].equalsIgnoreCase("invincible")) {
				if (!g.hasEnoughMoney(p, 25)) {
					p.sendMessage(ChatColor.RED
							+ "You don't have enough Money!");
					return true;
				}
				g.pay(p, 25);
				ppm.addPerk(p, Perks.INVINCIBLE);
			} else if (args[0].equalsIgnoreCase("zombieradar")) {
				if (!g.hasEnoughMoney(p, 5)) {
					p.sendMessage(ChatColor.RED
							+ "You don't have enough Money!");
					return true;
				}
				g.pay(p, 5);
				ppm.addPerk(p, Perks.ZOMBIERADAR);
			}else {
				p.sendMessage(ChatColor.RED + "Couldn't recognize entered Perk!");
				return true;
			}
			p.sendMessage(ChatColor.GREEN + "Succesfully added Perk");
			return true;
		}
		return true;
	}

	public String createHelpText(Player p) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.AQUA + "Buy Perks with WinPoints\n");
		sb.append("------------------------------------------------------------------------\nAvailable Perks:\n");
		sb.append("invisible - 15 WP - be invisible for 30 seconnds at the start\n");
		sb.append("invincible - 25 WP - survive one zombie attack\n");
		sb.append("zombieradar - 5 WP - Access to the /zr command that will give you zombie coords\n");
		sb.append("------------------------------------------------------------------------\n");
		sb.append("You can only have one perk per round!\n");
		sb.append("You currently have " + g.getMoney(p) + " WinPoints");
		return sb.toString();
	}
}
