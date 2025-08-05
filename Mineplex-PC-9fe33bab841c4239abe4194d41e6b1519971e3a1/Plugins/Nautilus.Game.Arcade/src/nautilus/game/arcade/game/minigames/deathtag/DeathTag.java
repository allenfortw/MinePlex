package nautilus.game.arcade.game.minigames.deathtag;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.minigames.deathtag.kits.*;
import nautilus.game.arcade.kit.Kit;

public class DeathTag extends TeamGame
{
	public DeathTag(ArcadeManager manager)
	{
		super(manager, GameType.DeathTag,

				new Kit[] 
						{ 
				new KitArcher(manager),
						},

						new String[] { 
				"Tag opponents by killing then.",
				"Revive tagged allies by standing near them.",
				"Win by Tagging all opponents."
		});
	}
}
