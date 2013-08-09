package com.wlan222.ZombieMinigame.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.wlan222.ZombieMinigame.Perks;
import com.wlan222.ZombieMinigame.ZombieMinigame;

public class PlayerPerksManager {

	private ZombieMinigame pl;

	public PlayerPerksManager(ZombieMinigame pl) {
		this.pl = pl;
		checkPerksFile();
	}

	public void checkPerksFile() {
		File f = new File(pl.getDataFolder(), "perks");
		if (!f.exists()) {
			HashMap<String, String> perks = new HashMap<String, String>();
			savePerks(perks);
		}
		loadPerks();
	}

	public void addPerk(Player p, String perk) {

		HashMap<String, String> perks = loadPerks();
		perks.put(p.getName(), perk);
		savePerks(perks);
	}

	public boolean hasPerk(Player p) {
		HashMap<String, String> perks = loadPerks();
		if (perks.containsKey(p.getName())) {
			return true;
		}
		return false;
	}

	public String getPerk(Player p) {
		return loadPerks().get(p.getName());
	}

	public void removePerk(Player p) {
		HashMap<String, String> perks = loadPerks();
		perks.remove(p.getName());
		savePerks(perks);
	}

	public void activatePerk(final Player p) {
		if (!hasPerk(p)) {
			return;
		}
		String perk = getPerk(p);

		if (perk.equals(Perks.INVISIBLE)) {
			PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY,
					0, 30);
			p.addPotionEffect(pe);
			pl.getServer().getScheduler()
					.scheduleSyncDelayedTask(pl, new Runnable() {

						@Override
						public void run() {
							removePerk(p);

						}

					}, 600);
		}
	}

	private void savePerks(HashMap<String, String> hashmap) {
		String filename = "perks";
		try {
			FileOutputStream fos = new FileOutputStream(new File(pl.getDataFolder(),filename));
			GZIPOutputStream gzos = new GZIPOutputStream(fos);
			ObjectOutputStream out = new ObjectOutputStream(gzos);
			out.writeObject(hashmap);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, String> loadPerks() {
		String filename = "perks";
		HashMap<String, String> out = null;
		try {
			FileInputStream fin = new FileInputStream(new File(pl.getDataFolder(),filename));
			GZIPInputStream gzin = new GZIPInputStream(fin);
			ObjectInputStream in = new ObjectInputStream(gzin);
			Object o = in.readObject();
			if (!(o instanceof HashMap<?, ?>)) {

				pl.getLogger().severe("Fatal Error! Perks file was corrupted!");
				out = new HashMap<String, String>();
			} else {
				out = (HashMap<String, String>) o;
			}
			in.close();
			gzin.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
}

