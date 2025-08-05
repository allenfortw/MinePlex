package nautilus.game.arcade.game.games.lobbers.trackers;

import org.bukkit.event.EventHandler;

import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.games.lobbers.events.TNTThrowEvent;
import nautilus.game.arcade.stats.StatTracker;

public class TrackerTNTThrown extends StatTracker<Game>
{
	
	public TrackerTNTThrown(Game game)
	{
		super(game);
	}
	
	@EventHandler
	public void onThrow(TNTThrowEvent event)
	{
		if (!getGame().IsLive())
			return;
	
		addStat(event.getPlayer(), "Thrown", 1, false, false);
	}
}
