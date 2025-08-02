package nautilus.game.arcade;

import java.util.HashMap;

import org.bukkit.ChatColor;

import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.minigames.dragons.Dragons;
import nautilus.game.arcade.game.minigames.escape.DragonEscape;
import nautilus.game.arcade.game.minigames.evolution.Evolution;
import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.quiver.Quiver;
import nautilus.game.arcade.game.minigames.snowfight.SnowFight;
import nautilus.game.arcade.game.minigames.runner.Runner;
import nautilus.game.arcade.game.minigames.spleef.Spleef;
import nautilus.game.arcade.game.minigames.turfforts.TurfForts;
import nautilus.game.arcade.game.standalone.bridge.Bridge;
import nautilus.game.arcade.game.standalone.castlesiege.CastleSiege;
import nautilus.game.arcade.game.standalone.smash.SuperSmash;
import nautilus.game.arcade.game.standalone.uhc.UHC;
import nautilus.game.arcade.game.standalone.zombiesurvival.ZombieSurvival;

public class GameFactory 
{
	private ArcadeManager _manager;

	public GameFactory(ArcadeManager gameManager) 
	{
		_manager = gameManager;
	}

	public Game CreateGame(GameType gameType, HashMap<String, ChatColor> pastTeams) 
	{
		if (gameType == GameType.Bridge)				return new Bridge(_manager);
		else if (gameType == GameType.Dragons)			return new Dragons(_manager);
		else if (gameType == GameType.DragonEscape)		return new DragonEscape(_manager);
		else if (gameType == GameType.CastleSiege)		return new CastleSiege(_manager, pastTeams);
		else if (gameType == GameType.MineWare)			return new MineWare(_manager);
		else if (gameType == GameType.Evolution)		return new Evolution(_manager);
		else if (gameType == GameType.Quiver)			return new Quiver(_manager);
		else if (gameType == GameType.Runner)			return new Runner(_manager);
		else if (gameType == GameType.SnowFight)		return new SnowFight(_manager);
		else if (gameType == GameType.Smash)			return new SuperSmash(_manager);	
		else if (gameType == GameType.Spleef)			return new Spleef(_manager);	
		else if (gameType == GameType.TurfForts)		return new TurfForts(_manager);
		else if (gameType == GameType.UHC)				return new UHC(_manager);
		else if (gameType == GameType.ZombieSurvival)	return new ZombieSurvival(_manager);
		else											return null;
	}
}
