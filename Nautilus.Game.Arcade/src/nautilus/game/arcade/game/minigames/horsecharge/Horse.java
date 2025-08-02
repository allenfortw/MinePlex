package nautilus.game.arcade.game.minigames.horsecharge;

import java.util.HashMap;

import org.bukkit.ChatColor;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.AsymTeamGame;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.minigames.horsecharge.kits.*;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;

public class Horse extends AsymTeamGame
{
	public Horse(ArcadeManager manager, HashMap<String, ChatColor> pastTeams)
	{
		super(manager, GameType.Horse,

				new Kit[] 
						{ 
				new KitHorseKnight(manager),
				new NullKit(manager),
				new KitDefenceArcher(manager),
						},

						new String[]
								{
				"Riders must charge the ruins",
				"Archers must defend the ruins",
				"Last team alive wins!",
				"",
				"Teams swap after game is over"
								}, pastTeams);
		
		DeathDropItems = false;
	}
	
	@Override
	public void ParseData()
	{

	}
	
	@Override
	public void RestrictKits()
	{
		for (Kit kit : GetKits())
		{
			for (GameTeam team : GetTeamList())
			{
				if (team.GetColor() == ChatColor.RED)
				{
					if (kit.GetName().contains("Defence"))
						team.GetRestrictedKits().add(kit);
				}
				else
				{
					if (kit.GetName().contains("Horseback"))
						team.GetRestrictedKits().add(kit);
				}
			}
		}
	}
}
