package com.wlan222.ZombieMinigame;

import org.bukkit.entity.Player;

public interface DisguiseManager {
	
	public boolean isDisguised(Player p);

	public void disguise(Player p);

	public void undisguise(Player p);
}
