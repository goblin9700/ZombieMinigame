package com.wlan222.ZombieMinigame.disguise;

import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.DisguiseManager;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseType;
import de.robingrether.idisguise.api.MobDisguise;

public class iDisguise implements DisguiseManager {

	@Override
	public boolean isDisguised(Player p) {
		return DisguiseAPI.isDisguised(p);
	}

	@Override
	public void disguise(Player p) {
		MobDisguise zombie = new MobDisguise(DisguiseType.ZOMBIE, false);
		DisguiseAPI.disguiseToAll(p, zombie);

	}

	@Override
	public void undisguise(Player p) {
		DisguiseAPI.undisguiseToAll(p);

	}

}
