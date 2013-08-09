package com.wlan222.ZombieMinigame.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.wlan222.ZombieMinigame.Game;
import com.wlan222.ZombieMinigame.Lobby;
import com.wlan222.ZombieMinigame.ZombieMinigame;

public class LobbyManager {

	private ZombieMinigame pl;
	private int lobbySize;
	private String idPrefix = "lobby-";
	private int lastId = 0;
	public ArrayList<Lobby> lobbyList = new ArrayList<Lobby>();
	public HashMap<String, Lobby> lobbyIDs = new HashMap<String, Lobby>();
	public HashMap<Player, Lobby> players = new HashMap<Player, Lobby>();
	public HashMap<String, Lobby> regions = new HashMap<String, Lobby>();
	private Game g;

	public LobbyManager(ZombieMinigame pl, int lobbySize, Game g) {
		this.pl = pl;
		this.lobbySize = lobbySize;
		this.g = g;
		g.setLM(this);
	}

	public void addLobby(com.wlan222.ZombieMinigame.DisguiseManager dm) {
		Lobby l = new Lobby(pl, idPrefix + lastId, lobbySize, g, this, dm);
		lobbyIDs.put(idPrefix + lastId, l);
		lastId++;
		lobbyList.add(l);

	}

}
