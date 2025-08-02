package nautilus.game.arcade.game;

import java.util.ArrayList;

import nautilus.game.arcade.GameType;

public class GameServerConfig 
{
	public String ServerType = null;
	public int MinPlayers = -1;
	public int MaxPlayers = -1;
	public ArrayList<GameType> GameList = new ArrayList<GameType>();
	
	public boolean IsValid()
	{
		return ServerType != null && MinPlayers != -1 && MaxPlayers != -1;
	}
}
