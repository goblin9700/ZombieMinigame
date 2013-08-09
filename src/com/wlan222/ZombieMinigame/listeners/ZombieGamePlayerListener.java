package com.wlan222.ZombieMinigame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.wlan222.ZombieMinigame.Lobby;
import com.wlan222.ZombieMinigame.Perks;
import com.wlan222.ZombieMinigame.manager.LobbyManager;
import com.wlan222.ZombieMinigame.manager.PlayerPerksManager;

public class ZombieGamePlayerListener implements Listener {

	private LobbyManager lm;
	private PlayerPerksManager ppm;

	public ZombieGamePlayerListener(LobbyManager lm, PlayerPerksManager ppm) {
		this.lm = lm;
		this.ppm = ppm;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (lm.players.containsKey(e.getPlayer())) {
			lm.players.get(e.getPlayer()).leave(e.getPlayer());
		}
	}

	@EventHandler
	public void onEntityByEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		if (lm.players.containsKey(p)) {
			if (!(e.getDamager() instanceof Player)) {
				e.setCancelled(true);
				return;
			}
			Player hurter = (Player) e.getDamager();
			if (!lm.players.get(p).isZombie(hurter)) {
				e.setCancelled(true);
				return;
			}
			if (!lm.players.get(p).isGameRunning()) {
				e.setCancelled(true);
				return;
			}
			if (lm.players.get(p).isZombie(p)) {
				e.setCancelled(true);
				return;
			}
			if (ppm.hasPerk(p)) {
				if (ppm.getPerk(p).equals(Perks.INVINCIBLE)) {
					p.teleport(lm.players.get(p).getHumanSpawn());
					ppm.removePerk(p);
					e.setCancelled(true);
					return;
				}
			}
			lm.players.get(p).zombify(p, hurter);
		}
	}

	@EventHandler
	public void onRegionLeave(RegionLeaveEvent e) {
		if (!lm.players.containsKey(e.getPlayer())) {
			return;
		}
		Lobby l = lm.players.get(e.getPlayer());
		if (e.getRegion().getId().equals(l.getRegion()) && e.isCancellable())

		{
			e.setCancelled(true);
			e.getPlayer().sendMessage(
					ChatColor.RED + "You cannot leave the combat area!");
		}
	}

	@EventHandler
	public void onRegionEnter(RegionEnterEvent e) {
		if (!lm.regions.containsKey(e.getRegion().getId())) {
			return;
		}
		Lobby l = lm.regions.get(e.getRegion().getId());
		if (l.isGameRunning() && !l.players.contains(e.getPlayer())) {
			e.getPlayer()
					.sendMessage(
							ChatColor.RED
									+ "There is a minigame in progress! You cannot enter the combat area!");
			e.setCancelled(true);
			return;
		}
	}
}
