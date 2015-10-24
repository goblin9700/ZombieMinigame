package com.wlan222.ZombieMinigame.disguise;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.DisguiseManager;

public class LibsDisguises implements DisguiseManager {

	@Override
	public boolean isDisguised(Player p) {
		return DisguiseAPI.isDisguised(p);
	}

	@Override
	public void disguise(Player p) {
		MobDisguise zombie = new MobDisguise(DisguiseType.ZOMBIE);
		DisguiseAPI.disguiseToAll(p, zombie);
	}

	@Override
	public void undisguise(Player p) {
		DisguiseAPI.undisguiseToAll(p);

	}

}
