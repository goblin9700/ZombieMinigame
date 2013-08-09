package com.wlan222.ZombieMinigame.disguise;

import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import com.wlan222.ZombieMinigame.DisguiseManager;

public class DisguiseCraft implements DisguiseManager {

	@Override
	public boolean isDisguised(Player p) {
		return pgDev.bukkit.DisguiseCraft.DisguiseCraft.getAPI().isDisguised(p);
	}

	@Override
	public void disguise(Player p) {
		Disguise zombie = new Disguise(pgDev.bukkit.DisguiseCraft.DisguiseCraft
				.getAPI().newEntityID(), DisguiseType.Zombie);
		pgDev.bukkit.DisguiseCraft.DisguiseCraft.getAPI().disguisePlayer(p,
				zombie);
	}

	@Override
	public void undisguise(Player p) {
		pgDev.bukkit.DisguiseCraft.DisguiseCraft.getAPI().undisguisePlayer(p);
	}

}
