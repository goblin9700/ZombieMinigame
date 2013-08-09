package com.wlan222.ZombieMinigame.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.wlan222.ZombieMinigame.Lobby;
import com.wlan222.ZombieMinigame.manager.LobbyManager;

public class SignListener implements Listener {

	private LobbyManager lm;

	public SignListener(LobbyManager lm) {
		this.lm = lm;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		Action action = e.getAction();
		ArrayList<Location> signs = new ArrayList<Location>();
		for (Lobby l : lm.lobbyList) {
			if (l.isSignSetup()) {
				signs.add(l.getSign().getLocation());
			}
		}
		if (action == Action.RIGHT_CLICK_BLOCK) {

			if (e.getClickedBlock().getType() == Material.WALL_SIGN
					|| e.getClickedBlock().getType() == Material.SIGN_POST) {

				Sign s = (Sign) e.getClickedBlock().getState();
				if (s.getLine(0).equalsIgnoreCase("[ZM]")) {
					if (lm.lobbyIDs.containsKey(s.getLine(1))) {
						lm.lobbyIDs.get(s.getLine(1)).setSign(s);
						for (Lobby l : lm.lobbyList) {
							l.updateSign();
						}
						e.getPlayer()
								.sendMessage(
										ChatColor.GREEN
												+ "Successfully created a Lobby Sign for "
												+ s.getLine(1));
						s.update();
					} else {
						e.getPlayer().sendMessage(
								ChatColor.RED + "Couldn't recognize Lobby ID");
					}
				} else if (signs.contains(e.getClickedBlock().getLocation())) {
					Lobby lobby = null;
					for (Lobby l : lm.lobbyList) {
						if (l.isSignSetup()) {
							if (s.getLocation().equals(
									l.getSign().getLocation())) {
								lobby = l;
							}
						}
					}
					if (lobby != null) {
						lobby.join(e.getPlayer());
					}
				}
			}
		}
	}
}
